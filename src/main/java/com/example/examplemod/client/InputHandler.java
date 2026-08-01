package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.gun.ShotProfile;
import com.example.examplemod.item.GunItem;
import com.example.examplemod.item.GunplayManager;
import com.example.examplemod.menu.GunContainer;
import com.example.examplemod.network.ServerboundFireGunPacket;
import com.example.examplemod.network.ServerboundOpenModifierMenuPacket;
import com.example.examplemod.network.ServerboundReloadGunPacket;
import com.example.examplemod.recoil.RecoilHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public final class InputHandler {

    private static boolean attackHeldLastTick = false;

    @SubscribeEvent
    static void onAttackInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !(player.getMainHandItem().getItem() instanceof GunItem)) {
            return;
        }
        event.setSwingHand(false);
        event.setCanceled(true);
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        handleFireInput(minecraft, player);

        while (Keybinds.OPEN_MODIFIER_MENU.consumeClick()) {
            if (player.getMainHandItem().getItem() instanceof GunItem) {
                ClientPacketDistributor.sendToServer(ServerboundOpenModifierMenuPacket.INSTANCE);
            }
        }

        while (Keybinds.RELOAD.consumeClick()) {
            if (minecraft.screen == null && player.getMainHandItem().getItem() instanceof GunItem
                    && !GunItem.isReloading(player.getMainHandItem())) {
                ClientPacketDistributor.sendToServer(ServerboundReloadGunPacket.INSTANCE);
            }
        }
    }

    private static void handleFireInput(Minecraft minecraft, LocalPlayer player) {
        boolean canInput = minecraft.screen == null && minecraft.mouseHandler.isMouseGrabbed();
        boolean attackHeld = canInput && minecraft.options.keyAttack.isDown();

        if (!(player.getMainHandItem().getItem() instanceof GunItem gunItem)) {
            attackHeldLastTick = attackHeld;
            return;
        }

        boolean triggerPulled = switch (gunItem.getGun().fireMode()) {
            case SEMI -> attackHeld && !attackHeldLastTick;
            case AUTO -> attackHeld;
        };
        attackHeldLastTick = attackHeld;

        if (triggerPulled) {
            tryFire(player, gunItem);
        }
    }

    private static void tryFire(LocalPlayer player, GunItem gunItem) {
        ItemStack held = player.getMainHandItem();
        // fixme: these conditions are thin and must be manually kept in sync
        if (player.getCooldowns().isOnCooldown(held) || GunItem.isReloading(held)) {
            return;
        }
        ShotProfile profile = GunplayManager.compose(gunItem.getGun(), new GunContainer(held), held);
        ClientPacketDistributor.sendToServer(new ServerboundFireGunPacket(player.getLookAngle()));
        // fixme: another thin client-server manually synced thingie
        if (!GunItem.getMagazine(held).isEmpty()) {
            RecoilManager.applyRecoil(profile);
        }
    }
}
