package com.example.examplemod.client.pose;

import com.example.examplemod.item.ReloadState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public final class GunArmPoses {
    public static final EnumProxy<HumanoidModel.ArmPose> PISTOL = new EnumProxy<>(
            HumanoidModel.ArmPose.class, false, false, (IArmPoseTransformer) GunArmPoses::applyPistolPose
    );

    public static final EnumProxy<HumanoidModel.ArmPose> RIFLE = new EnumProxy<>(
            HumanoidModel.ArmPose.class, true, true, (IArmPoseTransformer) GunArmPoses::applyRiflePose
    );

    private static <T extends HumanoidRenderState> void applyPistolPose(HumanoidModel<?> model, T renderState, HumanoidArm arm) {
        boolean holdingInRightArm = arm == HumanoidArm.RIGHT;
        ReloadState reloadState = ReloadState.get(renderState.getMainHandItemStack()); // fixme: mainhand hardcode
        if (reloadState != null && !reloadState.isFinished()) {
            animateCrossbowCharge(model.rightArm, model.leftArm, reloadState.progress(), reloadState.duration(), holdingInRightArm);
        } else {
            var head = model.head;
            ModelPart armModel = model.getArm(arm);
            armModel.yRot = (holdingInRightArm ? -0.15F : 0.15F) + head.yRot;
            armModel.xRot = -1.5F + head.xRot;
        }
    }

    private static <T extends HumanoidRenderState> void applyRiflePose(HumanoidModel<?> model, T renderState, HumanoidArm arm) {
        boolean holdingInRightArm = arm == HumanoidArm.RIGHT;
        ReloadState reloadState = ReloadState.get(renderState.getMainHandItemStack()); // fixme: mainhand hardcode
        if (reloadState != null && !reloadState.isFinished()) {
            animateCrossbowCharge(model.rightArm, model.leftArm, reloadState.progress(), reloadState.duration(), holdingInRightArm);
        } else {
            var head = model.head;
            ModelPart holdingArm = holdingInRightArm ? model.rightArm : model.leftArm;
            ModelPart shootingArm = holdingInRightArm ? model.leftArm : model.rightArm;
            holdingArm.yRot = (holdingInRightArm ? -0.1F : 0.1F) + head.yRot;
            shootingArm.yRot = (holdingInRightArm ? 0.8F : -0.8F) + head.yRot;
            holdingArm.xRot = (-(float) Math.PI / 2F) + head.xRot + 0.1F;
            shootingArm.xRot = -1.5F + head.xRot;
        }
    }

    public static void animateCrossbowCharge(ModelPart rightArm, ModelPart leftArm, float maxCrossbowChargeDuration, float ticksUsingItem, boolean holdingInRightArm) {
        ModelPart holdingArm = holdingInRightArm ? rightArm : leftArm;
        ModelPart pullingArm = holdingInRightArm ? leftArm : rightArm;
        holdingArm.yRot = holdingInRightArm ? -0.8F : 0.8F;
        holdingArm.xRot = -0.97079635F;
        pullingArm.xRot = holdingArm.xRot;
        float useTicks = Mth.clamp(ticksUsingItem, 0.0F, maxCrossbowChargeDuration);
        float lerpAlpha = useTicks / maxCrossbowChargeDuration;
        pullingArm.yRot = Mth.lerp(lerpAlpha, 0.4F, 0.85F) * (float) (holdingInRightArm ? 1 : -1);
        pullingArm.xRot = Mth.lerp(lerpAlpha, pullingArm.xRot, (-(float) Math.PI / 2F));
    }
}
