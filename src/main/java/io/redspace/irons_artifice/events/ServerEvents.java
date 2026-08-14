package io.redspace.irons_artifice.events;

import com.geckolib.animatable.GeoItem;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.gun.ShotProfile;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.item.GunplayManager;
import io.redspace.irons_artifice.item.ReloadState;
import io.redspace.irons_artifice.item.TopLoadConfig;
import io.redspace.irons_artifice.network.ClientboundEquipSoundPacket;
import io.redspace.irons_artifice.network.ClientboundGunAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class ServerEvents {


    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        //    @SubscribeEvent
//    public static void onDamage(LivingIncomingDamageEvent event) {
//        // fixme: https://github.com/neoforged/NeoForge/issues/3348
//        if (event.getSource().getDirectEntity() instanceof Bullet) {
//            event.getContainer().setPostAttackInvulnerabilityTicks(0);
//        }
//    }
        if (event.getSource().getDirectEntity() instanceof Bullet) {
            event.getEntity().invulnerableTime = 0;
        }
    }

    @SubscribeEvent
    public static void onMobEffectApplication(MobEffectEvent.Applicable event) {
        if (event.getEffectSource() instanceof AreaEffectCloud areaEffectCloud &&
                areaEffectCloud.getOwner() == event.getEntity() &&
                areaEffectCloud.getPersistentData().getBooleanOr("irons_artifice:venom_cloud", false)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        var entity = event.getEntity();
        var equippedStack = event.getTo();
        var fromStack = event.getFrom();
        if (entity.level() instanceof ServerLevel serverLevel &&
                event.getSlot().equals(EquipmentSlot.MAINHAND) && //fixme: hardcoded mainhand
                !equippedStack.isEmpty() && equippedStack.getItem() instanceof GunItem gunItem &&
                (gunItem != fromStack.getItem() || GeoItem.getId(equippedStack) != GeoItem.getId(fromStack))) {
            if (GunItem.isReloading(equippedStack)) {
                resumeReloadState(serverLevel, gunItem, equippedStack, entity);
            } else {
                performEquipEffects(serverLevel, gunItem, entity, equippedStack);
            }
        }
    }

    private static void performEquipEffects(ServerLevel serverLevel, GunItem gunItem, LivingEntity entity, ItemStack equippedStack) {
        ClientboundGunAnimationPacket packet = new ClientboundGunAnimationPacket(entity.getId(), GeoItem.getOrAssignId(equippedStack, serverLevel),
                equippedStack == entity.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
                "equip", 1.0, 0);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, packet);
        if (gunItem.getEquipSound() != null && entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new ClientboundEquipSoundPacket(SoundSource.PLAYERS, gunItem));
        }
    }

    private static void resumeReloadState(ServerLevel serverLevel, GunItem gunItem, ItemStack equippedStack, LivingEntity entity) {
        ShotProfile profile = GunplayManager.compose(entity, gunItem.getGunProfile(), equippedStack);
        double reloadSpeedMultiplier = profile.value(ShotComponents.RELOAD_SPEED_MULTIPLIER);
        ReloadState reloadState = ReloadState.get(equippedStack);
        double progress = reloadState.progress();
        double skipAt = 0;
        double skipTo = 0;
        TopLoadConfig topLoadConfig = gunItem.getGunProfile().topLoadConfig();
        if (topLoadConfig != null && reloadState.topLoadCount() > 0) {
            skipAt = topLoadConfig.loopStart();
            skipTo = topLoadConfig.resumeFrom(reloadState.topLoadCount());
        }
        ClientboundGunAnimationPacket packet = new ClientboundGunAnimationPacket(entity.getId(), GeoItem.getOrAssignId(equippedStack, serverLevel),
                equippedStack == entity.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
                "reload", reloadSpeedMultiplier, progress, skipAt, skipTo);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, packet);
    }
}
