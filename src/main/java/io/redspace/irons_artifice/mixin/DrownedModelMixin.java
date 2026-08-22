package io.redspace.irons_artifice.mixin;

import io.redspace.irons_artifice.client.gun.GunArmPoses;
import io.redspace.irons_artifice.gun.ArmPoseKind;
import io.redspace.irons_artifice.item.GunItem;
import net.minecraft.client.model.monster.zombie.DrownedModel;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrownedModel.class)
public class DrownedModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ZombieRenderState;)V", at = @At("TAIL"))
    private void irons_artifice$drownedGunAnimation(ZombieRenderState state, CallbackInfo ci) {
        if (state.leftHandItemStack.getItem() instanceof GunItem gunItem) {
            var pose = gunItem.getGun().armPoseKind() == ArmPoseKind.PISTOL ? GunArmPoses.PISTOL.getValue() : GunArmPoses.RIFLE.getValue();
            pose.applyTransform((DrownedModel) (Object) this, state, HumanoidArm.LEFT);
        }
        if (state.rightHandItemStack.getItem() instanceof GunItem gunItem) {
            var pose = gunItem.getGun().armPoseKind() == ArmPoseKind.PISTOL ? GunArmPoses.PISTOL.getValue() : GunArmPoses.RIFLE.getValue();
            pose.applyTransform((DrownedModel) (Object) this, state, HumanoidArm.RIGHT);
        }
    }
}
