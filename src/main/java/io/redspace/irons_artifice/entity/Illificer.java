package io.redspace.irons_artifice.entity;

import io.redspace.irons_artifice.config.ServerConfig;
import io.redspace.irons_artifice.entity.ai.RangedGunAttackGoal;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.menu.GunContainer;
import io.redspace.irons_artifice.modifier.ModifierItem;
import io.redspace.irons_artifice.registry.EntityRegistry;
import io.redspace.irons_artifice.registry.ItemRegistry;
import io.redspace.irons_artifice.registry.LootTableRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class Illificer extends AbstractIllager implements IGunslingerMob {
    public Illificer(EntityType<? extends Illificer> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.MAX_HEALTH, 36.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    public static ItemStack createLoadout(ServerLevel level) {
        ItemStack gun = new ItemStack(ItemRegistry.ARQUEBUS.get());
        List<ItemStack> rolled = rollLoadout(level);
        GunContainer container = new GunContainer(gun);
        int slot = 0;
        for (ItemStack stack : rolled) {
            if (slot >= container.getContainerSize()) {
                break;
            }
            if (!(stack.getItem() instanceof ModifierItem)) {
                continue;
            }
            container.setItem(slot++, stack.copyWithCount(1));
        }
        container.setChanged();
        return gun;
    }

    public static List<ItemStack> rollLoadout(ServerLevel level) {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(LootTableRegistry.ILLIFICER_LOADOUT);
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        return table.getRandomItems(params);
    }

    public static List<ItemStack> getInstalledModifiers(ItemStack gun) {
        GunContainer container = new GunContainer(gun);
        List<ItemStack> mods = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ModifierItem) {
                mods.add(stack.copy());
            }
        }
        return mods;
    }

    private static boolean isReplaceableEvokerSpawn(EntitySpawnReason reason) {
        return reason == EntitySpawnReason.STRUCTURE
                || reason == EntitySpawnReason.EVENT
                || reason == EntitySpawnReason.NATURAL
                || reason == EntitySpawnReason.CHUNK_GENERATION;
    }

    @SubscribeEvent
    public static void replaceEvoker(FinalizeSpawnEvent event) {
        if (!(event.getEntity() instanceof Evoker evoker)) {
            return;
        }
        if (!(evoker.level() instanceof ServerLevel level)) {
            return;
        }
        if (!isReplaceableEvokerSpawn(event.getSpawnType())) {
            return;
        }
        if (level.getRandom().nextDouble() >= ServerConfig.ILLIFICER_REPLACE_EVOKER_CHANCE.getAsDouble()) {
            return;
        }

        Illificer illificer = EntityRegistry.ILLIFICER.get().create(level, event.getSpawnType());
        if (illificer == null) {
            return;
        }

        illificer.snapTo(evoker.getX(), evoker.getY(), evoker.getZ(), evoker.getYRot(), evoker.getXRot());
        illificer.setYHeadRot(evoker.getYHeadRot());
        illificer.setYBodyRot(evoker.yBodyRot);

        Raid raid = evoker.getCurrentRaid();
        int wave = evoker.getWave();
        boolean canJoinRaid = evoker.canJoinRaid();

        event.setSpawnCancelled(true);
        if (evoker.hasActiveRaid() && raid != null) {
            raid.removeFromRaid(level, evoker, true);
        }

        illificer.setCanJoinRaid(canJoinRaid);
        illificer.finalizeSpawn(level,event.getDifficulty(), event.getSpawnType(), null);
        level.addFreshEntityWithPassengers(illificer);

        if (raid != null && canJoinRaid) {
            raid.joinRaid(level, wave, illificer, null, true);
        }
    }

    @SubscribeEvent
    public static void dropLoadoutModifier(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Illificer illificer)) {
            return;
        }
        ItemStack gun = illificer.getMainHandItem();
        if (!(gun.getItem() instanceof GunItem)) {
            return;
        }
        List<ItemStack> mods = getInstalledModifiers(gun);
        if (mods.isEmpty()) {
            return;
        }
        ItemStack drop = mods.get(illificer.getRandom().nextInt(mods.size())).copyWithCount(1);
        event.getDrops().add(new ItemEntity(illificer.level(), illificer.getX(), illificer.getY(), illificer.getZ(), drop));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedGunAttackGoal<>(this, 24, 15, 45, 40, 80));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnGroupData) {
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        if (level() instanceof ServerLevel serverLevel) {
            setItemSlot(EquipmentSlot.MAINHAND, createLoadout(serverLevel));
        }
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public void applyRaidBuffs(ServerLevel level, int wave, boolean isCaptain) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (isAggressive() && !getMainHandItem().isEmpty()) {
            return IllagerArmPose.CROSSBOW_HOLD;
        }
        return IllagerArmPose.NEUTRAL;
    }

    @Override
    public void onVolleyEnd() {
        if (this.getRandom().nextBoolean()) {
            this.playSound(getCelebrateSound());
        }
    }
}
