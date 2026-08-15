package io.redspace.irons_artifice.mixin;

import io.redspace.irons_artifice.item.FireDelayState;
import io.redspace.irons_artifice.item.ReloadState;
import io.redspace.irons_artifice.registry.DataComponentRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "handleCreativeModeItemAdd", at = @At("HEAD"), cancellable = true)
    private void cancelGunItemSpam(ItemStack clicked, int slot, CallbackInfo ci) {
        if (ReloadState.has(clicked) || FireDelayState.isActive(clicked)) {
            ItemStack last = Minecraft.getInstance().player.getInventory().getItem(slot - 36).copy();
            ItemStack current = clicked.copy();
            last.remove(DataComponentRegistry.RELOAD_STATE);
            last.remove(DataComponentRegistry.FIRE_DELAY_STATE);
            current.remove(DataComponentRegistry.RELOAD_STATE);
            current.remove(DataComponentRegistry.FIRE_DELAY_STATE);
            if (ItemStack.isSameItemSameComponents(last, current)) {
                ci.cancel();
            }
        }
    }
}
