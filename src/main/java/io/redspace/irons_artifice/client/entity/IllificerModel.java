package io.redspace.irons_artifice.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class IllificerModel extends IllagerModel<IllificerRenderer.GunIllagerRenderState> {
    protected final ModelPart rightArm;
    protected final ModelPart leftArm;

    public IllificerModel(ModelPart root) {
        super(root);
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.getHat().visible = true;
    }

    @Override
    public void setupAnim(IllificerRenderer.GunIllagerRenderState state) {
        super.setupAnim(state);
        var humanoidProxy = new HumanoidModel<>(this.root);
        if (state.mobGunPose == IllificerRenderer.MobGunPose.HUMANOID) {
            state.humanoidPose.applyTransform(humanoidProxy, state, state.mainArm);
        } else if (state.mobGunPose == IllificerRenderer.MobGunPose.IDLE) {
            var arm = getArm(state.mainArm);
            arm.xRot *= 0.25f;
            arm.xRot -= Mth.PI / 6f;
        }
    }

    @Override
    public void translateToHand(IllagerRenderState state, HumanoidArm arm, PoseStack poseStack) {
        super.translateToHand(state, arm, poseStack);
        if (state instanceof IllificerRenderer.GunIllagerRenderState gunState && gunState.mobGunPose == IllificerRenderer.MobGunPose.IDLE && arm == state.mainArm) {
//            var hand = getArm(arm);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.translate(0,-1,0);
        }
    }

    protected ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }
}
