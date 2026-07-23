package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.gun.ShotProfile;
import com.example.examplemod.item.GunItem;
import com.example.examplemod.item.GunplayManager;
import com.example.examplemod.menu.GunContainer;
import com.example.examplemod.network.ServerboundFireGunPacket;
import com.example.examplemod.network.ServerboundOpenModifierMenuPacket;
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

    @SubscribeEvent
    static void onAttackInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        if (!(player.getMainHandItem().getItem() instanceof GunItem gunItem)) {
            return;
        }

        event.setSwingHand(false);
        event.setCanceled(true);

        ClientPacketDistributor.sendToServer(ServerboundFireGunPacket.INSTANCE);
        predictRecoil(player, gunItem);
    }

    private static void predictRecoil(LocalPlayer player, GunItem gunItem) {
        ItemStack held = player.getMainHandItem();
        // todo: this layer is thin and must be manually kept in sync with future conditions. shot profile itself should be able to judge whether it can shoot or something
        if (player.getCooldowns().isOnCooldown(held) || GunItem.getMagazine(held).isEmpty()) {
            return;
        }
        ShotProfile profile = GunplayManager.compose(gunItem.getGun(), new GunContainer(held), player, held, player.level());
        // deterministic and server-synced "spray pattern" based off of the index of the bullet being fired
        int bulletIndex = gunItem.magazineCapacity() - GunItem.getMagazine(held).count();
        RecoilManager.applyRecoil((float) profile.value(ShotComponents.CAMERA_RECOIL), bulletIndex);
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        while (Keybinds.OPEN_MODIFIER_MENU.consumeClick()) {
            if (player.getMainHandItem().getItem() instanceof GunItem) {
                ClientPacketDistributor.sendToServer(ServerboundOpenModifierMenuPacket.INSTANCE);
            }
        }
    }
}
