package io.redspace.irons_artifice.mixin;

import io.redspace.irons_artifice.item.GunItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @ModifyVariable(
            method = "renderArmWithItem",
            at = @At("HEAD"),
            argsOnly = true,
            name = "inverseArmHeight")
    private float irons_artifice$zeroGunEquipOffset(
            float inverseArmHeight,
            AbstractClientPlayer player,
            float frameInterp,
            float xRot,
            InteractionHand hand,
            float attack,
            ItemStack itemStack
    ) {
        return itemStack.getItem() instanceof GunItem ? 0.0F : inverseArmHeight;
    }
}
