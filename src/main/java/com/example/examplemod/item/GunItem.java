package com.example.examplemod.item;

import com.example.examplemod.data.ReloadResult;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.gun.GunProfile;
import com.example.examplemod.gun.ShotProfile;
import com.example.examplemod.menu.GunContainer;
import com.example.examplemod.registry.DataComponentRegistry;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class GunItem extends BaseGeoItem {
    public static final String TRIGGERED_ANIMATION_CONTROLLER = "Actions";
    public static final String IDLE_ANIMATION_CONTROLLER = "gun_animation_controller";
    private final GunProfile gunProfile;

    public GunItem(Properties properties, GunProfile gunProfile) {
        super(properties.component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.CONTAINER, true)));
        this.gunProfile = gunProfile;
    }

    public GunProfile getGun() {
        return gunProfile;
    }

    public int magazineCapacity() {
        return gunProfile.magazineCapacity();
    }

    public static MagazineContents getMagazine(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.MAGAZINE.get(), MagazineContents.EMPTY);
    }

    public static void setMagazine(ItemStack stack, MagazineContents magazine) {
        stack.set(DataComponentRegistry.MAGAZINE.get(), magazine);
    }

    public static void startReload(ItemStack stack, int duration) {
        stack.set(DataComponentRegistry.RELOAD_STATE, new ReloadState(0, duration));
    }

    public static boolean isReloading(ItemStack stack) {
        return stack.has(DataComponentRegistry.RELOAD_STATE);
    }

    public GunProfile getGunProfile() {
        return gunProfile;
    }

    @Override
    public void inventoryTick(@NonNull ItemStack itemStack, @NonNull ServerLevel level, @NonNull Entity owner, @Nullable EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, owner, slot);
        if (slot != EquipmentSlot.MAINHAND || !(owner instanceof Player player)) {
            // fixme: will this cause issue in offhand? i think a lot of things (animations, dual-wielding) need specific offhand handling
            //  not a v1 concern
            return;
        }
        if (isReloading(itemStack) && ReloadState.tickReload(itemStack)) {
            ReloadResult result = GunplayManager.attemptFinishReload(player, itemStack);
            playReloadFeedback(level, player, result);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && !isReloading(stack)) {
            ReloadResult result = GunplayManager.attemptStartReload(player, stack);
            playReloadFeedback(level, player, result);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        ShotProfile shotProfile = GunplayManager.compose(this.gunProfile, new GunContainer(itemStack), itemStack);
        String damage = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(shotProfile.value(ShotComponents.DAMAGE));
        int bulletCount = (int) shotProfile.value(ShotComponents.PROJECTILE_COUNT);
//        float bulletSpeed = (float) shotProfile.value(ShotComponents.BULLET_SPEED);
        String fireRate = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(20 / shotProfile.value(ShotComponents.FIRE_DELAY));
        String reloadTime = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(gunProfile.reloadTimeTicks() / 20f);
        if (bulletCount > 1) {
            builder.accept(Component.literal(" ").append(Component.translatable("examplemod.tooltip.damage_per_bullet", damage, bulletCount).withStyle(ChatFormatting.DARK_GREEN)));
            builder.accept(Component.literal(" ").append(Component.translatable("examplemod.tooltip.bullet_count", bulletCount).withStyle(ChatFormatting.DARK_GREEN)));
        } else {
            builder.accept(Component.literal(" ").append(Component.translatable("examplemod.tooltip.damage", damage).withStyle(ChatFormatting.DARK_GREEN)));
        }
//        builder.accept(Component.translatable("examplemod.tooltip.bullet_speed", bulletSpeed));
        builder.accept(Component.literal(" ").append(Component.translatable("examplemod.tooltip.fire_rate", fireRate).withStyle(ChatFormatting.DARK_GREEN)));
        builder.accept(Component.literal(" ").append(Component.translatable("examplemod.tooltip.reload_time", reloadTime).withStyle(ChatFormatting.DARK_GREEN)));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    private static void playReloadFeedback(Level level, Player player, ReloadResult result) {
        switch (result) {
            case FINISHED_RELOAD -> level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_LOADING_END.value(), SoundSource.PLAYERS, 0.8F, 1.0F);
            case STARTING_RELOAD -> level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_LOADING_START.value(), SoundSource.PLAYERS, 0.8F, 1.0F);
            case NO_AMMO -> level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.6F, 1.0F);
            case ALREADY_FULL -> level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.NOTE_BLOCK_DIDGERIDOO, SoundSource.PLAYERS, 0.6F, 1.0F);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (isReloading(stack)) {
            return (int) (stack.get(DataComponentRegistry.RELOAD_STATE).percent(0) * 13);
        } else {
            int count = getMagazine(stack).count();
            return Mth.clamp(Math.round(count * 13.0F / magazineCapacity()), 0, 13);
        }
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (isReloading(stack)) {
            return 0xAAAAAA;
        } else {
            // hell yeah
            return 0xFFAA00;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(IDLE_ANIMATION_CONTROLLER, this::gunAnimationStateHandler)
        );
        controllers.add(new AnimationController<>("Actions", test -> PlayState.STOP) {
                    @Override
                    protected void initializeNewAnimation(GeoAnimatable animatable, GeoRenderState renderState, GeoModel<GeoAnimatable> geoModel, double prevAnimSpeed, int prevTransitionTicks) {
                        double offset = timelineTime;
                        super.initializeNewAnimation(animatable, renderState, geoModel, prevAnimSpeed, prevTransitionTicks);
                        if (offset > 0) {
                            timelineTime = offset;
                        }
                    }
                }
                        .triggerableAnim("fire", RawAnimation.begin().thenPlay("fire"))
                        .triggerableAnim("reload", RawAnimation.begin().thenPlay("reload"))
                        .triggerableAnim("equip", RawAnimation.begin().thenPlay("equip"))
        );
    }

    private PlayState gunAnimationStateHandler(AnimationTest<GunItem> animationTest) {
        animationTest.setAnimation(RawAnimation.begin().thenPlayAndHold("idle"));
        return PlayState.CONTINUE;
    }
}
