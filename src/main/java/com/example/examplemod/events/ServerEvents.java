package com.example.examplemod.events;

import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.entity.Bullet;
import com.example.examplemod.gun.ShotProfile;
import com.example.examplemod.item.GunItem;
import com.example.examplemod.item.GunplayManager;
import com.example.examplemod.item.ReloadState;
import com.example.examplemod.menu.GunContainer;
import com.example.examplemod.network.ClientboundGunAnimationPacket;
import com.example.examplemod.registry.DataComponentRegistry;
import com.geckolib.animatable.GeoItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
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
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        var entity = event.getEntity();
        var equippedStack = event.getTo();
        var fromStack = event.getFrom();
        if (entity.level() instanceof ServerLevel serverLevel &&
                event.getSlot().equals(EquipmentSlot.MAINHAND) && //fixme: hardcoded mainhand
                !equippedStack.isEmpty() && equippedStack.getItem() instanceof GunItem gunItem &&
                (gunItem != fromStack.getItem() || GeoItem.getId(equippedStack) != GeoItem.getId(fromStack))) {
            ClientboundGunAnimationPacket packet;
            if (GunItem.isReloading(equippedStack)) {
                ShotProfile profile = GunplayManager.compose(gunItem.getGunProfile(), new GunContainer(equippedStack), equippedStack);
                double reloadSpeedMultiplier = profile.value(ShotComponents.RELOAD_SPEED_MULTIPLIER);
                ReloadState reloadState = equippedStack.get(DataComponentRegistry.RELOAD_STATE);
                double progress = reloadState.animationProgressSeconds(gunItem.getGunProfile());
                packet = new ClientboundGunAnimationPacket(entity.getId(), GeoItem.getOrAssignId(equippedStack, serverLevel),
                        equippedStack == entity.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
                        "reload", reloadSpeedMultiplier, progress);
            } else {
                packet = new ClientboundGunAnimationPacket(entity.getId(), GeoItem.getOrAssignId(equippedStack, serverLevel),
                        equippedStack == entity.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
                        "equip", 1.0, 0);
            }
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, packet);
        }
    }
}
