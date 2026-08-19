package io.redspace.irons_artifice.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.irons_artifice.client.BayonetAnimations;
import io.redspace.irons_artifice.item.GunItem;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.KineticWeapon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpearAnimations.class)
public class SpearAnimationsMixin {
    @Inject(method = "firstPersonUse", at = @At("HEAD"), cancellable = true)
    private static void irons_artifice$handleBayonetAnimation(float ticksSinceKineticHitFeedback, PoseStack poseStack, float timeHeld, HumanoidArm arm, ItemStack itemStack, CallbackInfo ci) {
        if (!(itemStack.getItem() instanceof GunItem gunItem)) {
            return;
        }
        KineticWeapon kineticWeapon = itemStack.get(DataComponents.KINETIC_WEAPON);
        if (kineticWeapon == null) {
            return;
        }
        BayonetAnimations.firstPersonUse(ticksSinceKineticHitFeedback, poseStack, timeHeld, arm, itemStack);
        ci.cancel();
    }
}
