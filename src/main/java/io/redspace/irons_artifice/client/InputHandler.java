package io.redspace.irons_artifice.client;

import io.redspace.irons_artifice.IronsArtifice;
import io.redspace.irons_artifice.client.sounds.EquipSoundInstance;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.gun.ShotProfile;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.item.GunplayManager;
import io.redspace.irons_artifice.network.ServerboundFireGunPacket;
import io.redspace.irons_artifice.network.ServerboundOpenModifierMenuPacket;
import io.redspace.irons_artifice.network.ServerboundReloadGunPacket;
import io.redspace.irons_artifice.registry.ItemRegistry;
import io.redspace.irons_artifice.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = IronsArtifice.MODID, value = Dist.CLIENT)
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
        ItemStack held = player.getMainHandItem();
        ShotProfile profile = GunplayManager.compose(player, gunItem.getGun(), held);

        boolean triggerPulled = switch (profile.fireMode()) {
            case SEMI -> attackHeld && !attackHeldLastTick;
            case AUTO -> attackHeld;
        };
        attackHeldLastTick = attackHeld;

        if (triggerPulled) {
            tryFire(profile, player, gunItem);
        }
    }

    private static void tryFire(ShotProfile profile, LocalPlayer player, GunItem gunItem) {
        if (GunplayManager.tryFire(player, player.getLookAngle())) {
            ClientPacketDistributor.sendToServer(new ServerboundFireGunPacket(player.getLookAngle()));
            RecoilManager.applyRecoil(profile);
            // fixme: this is dumb but why is it so hard to do this any other way
            if (profile.itemStack().is(ItemRegistry.BLACKPOWDER_REVOLVER)) {
                int delay = (int) (20 * 0.5 / ((profile.value(ShotComponents.FIRE_RATE) + 2) / 3));
                Minecraft.getInstance().getSoundManager().playDelayed(new EquipSoundInstance(SoundRegistry.COCK_HAMMER.get(), SoundSource.PLAYERS,
                        1f, .9f + player.getRandom().nextFloat() * 0.2f,
                        player.getRandom(), gunItem, delay), delay);
            }
        }
    }
}
