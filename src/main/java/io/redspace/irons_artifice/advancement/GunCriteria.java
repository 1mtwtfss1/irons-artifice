package io.redspace.irons_artifice.advancement;

import io.redspace.irons_artifice.api.GunShootEvent;
import io.redspace.irons_artifice.data.RecentShots;
import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.registry.CriterionRegistry;
import io.redspace.irons_artifice.registry.DataAttachmentRegistry;
import io.redspace.irons_artifice.registry.DataComponentRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber
public final class GunCriteria {
    private GunCriteria() {
    }

    public static ShotCombatTracker tracker(ServerPlayer player) {
        return player.getData(DataAttachmentRegistry.SHOT_COMBAT);
    }

    public static void markInstaReload(ServerPlayer player) {
        ShotCombatTracker state = tracker(player);
        state.markInstaReload(player.level().getGameTime());
        player.setData(DataAttachmentRegistry.SHOT_COMBAT, state);
    }

    public static void triggerModified(ServerPlayer player, ItemStack gun, int occupied, int capacity) {
        CriterionRegistry.GUN_MODIFIED.get().trigger(player, gun, occupied, capacity);
    }

    @SubscribeEvent
    public static void onShoot(GunShootEvent.Post event) {
        LivingEntity shooter = event.getEntity();
        RecentShots.trackShot(shooter);
        if (shooter instanceof ServerPlayer player) {
            CriterionRegistry.SHOT_GUN.get().trigger(player, event.getShotProfile().itemStack(), RecentShots.count(player));
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        Bullet bullet = bulletFrom(event.getSource());
        if (bullet == null) {
            return;
        }
        ShotRecord record = bullet.getShotRecord();
        if (record == null) {
            return;
        }
        LivingEntity victim = event.getEntity();
        int pellets = 0;
        if (record.root()) {
            ShotCombatTracker state = tracker(player);
            pellets = state.recordRootHit(record.fireId(), victim.getId());
            player.setData(DataAttachmentRegistry.SHOT_COMBAT, state);
        }
        triggerCombat(player, false, event.getInflictedDamage(), player.distanceTo(victim), pellets, 0, record, bullet.getProfile().itemStack(), victim, GunCombatSource.BULLET);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        LivingEntity victim = event.getEntity();
        Bullet bullet = bulletFrom(event.getSource());
        if (bullet != null) {
            ShotRecord record = bullet.getShotRecord();
            if (record == null) {
                return;
            }
            ShotCombatTracker state = tracker(player);
            int lineageKills = state.recordKill(record.lineageId(), victim.getUUID());
            player.setData(DataAttachmentRegistry.SHOT_COMBAT, state);
            triggerCombat(player, true, victim.getMaxHealth(), player.distanceTo(victim), 0, lineageKills, record, bullet.getProfile().itemStack(), victim, GunCombatSource.BULLET);
            return;
        }
        ItemStack gun = player.getMainHandItem();
        if (gun.getItem() instanceof GunItem && gun.has(DataComponents.KINETIC_WEAPON)
                && (gun.has(DataComponentRegistry.ATTACHMENT.get()) || gun.has(DataComponents.KINETIC_WEAPON))) {
            triggerCombat(player, true, victim.getMaxHealth(), player.distanceTo(victim), 0, 1, null, gun, victim, GunCombatSource.BAYONET);
        }
    }

    private static void triggerCombat(
            ServerPlayer player,
            boolean killed,
            float damage,
            double distance,
            int pellets,
            int lineageKills,
            @Nullable ShotRecord record,
            ItemStack gun,
            Entity victim,
            GunCombatSource source
    ) {
        ShotCombatTracker state = tracker(player);
        CriterionRegistry.GUN_COMBAT.get().trigger(player, new GunCombatTrigger.CombatSnapshot(
                killed,
                damage,
                distance,
                pellets,
                lineageKills,
                record != null && record.ricocheted(),
                record != null && record.fullMagazine(),
                state.instaReloaded(player.level().getGameTime()),
                player.isUnderWater(),
                source,
                gun,
                victim
        ));
    }

    private static @Nullable Bullet bulletFrom(DamageSource source) {
        if (source.getDirectEntity() instanceof Bullet bullet) {
            return bullet;
        }
        if (source.getEntity() instanceof Bullet bullet) {
            return bullet;
        }
        return null;
    }
}
