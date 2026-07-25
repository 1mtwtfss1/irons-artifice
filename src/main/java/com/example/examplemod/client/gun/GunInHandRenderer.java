package com.example.examplemod.client.gun;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.GunItem;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class GunInHandRenderer extends GeoItemRenderer<GunItem> {
    public GunInHandRenderer(GeoModel<GunItem> model) {
        super(model);
    }

    @Override
    public void preRenderPass(@NonNull RenderPassInfo<GeoRenderState> renderPassInfo, @NonNull SubmitNodeCollector renderTasks) {
        super.preRenderPass(renderPassInfo, renderTasks);
        // todo: cache bones
        List<GeoBone> bones = List.of(renderPassInfo.model().getBone("right_arm").orElseThrow(), renderPassInfo.model().getBone("left_arm").orElseThrow());
        final GeoRenderState renderState = renderPassInfo.renderState();
        for (var bone : bones) {
            AbstractClientPlayer player = Minecraft.getInstance().player;
            Identifier skinTexture = player.getSkin().body().texturePath();
            final RenderType renderType = getRenderType(renderState, skinTexture);
            renderPassInfo.addPerBoneRender(bone, (renderPassInfo1, bone1, renderTasks1) -> {
                        renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, vertexConsumer) ->
                                renderFirstPersonHand(renderState, pose, bone, renderPassInfo, vertexConsumer, -1));
                    }
            );
        }
    }

    protected void renderFirstPersonHand(GeoRenderState renderState, PoseStack.Pose pose, GeoBone bone, RenderPassInfo<GeoRenderState> renderPassInfo, VertexConsumer vertexConsumer, int renderColor) {
        final PoseStack poseStack = new PoseStack();
        poseStack.last().set(pose);
//        bone.positionAndRender(renderPassInfo, vertexConsumer, renderPassInfo.packedLight(), renderPassInfo.packedOverlay(), renderColor);
        bone.render(renderPassInfo, poseStack, vertexConsumer, renderPassInfo.packedLight(), renderPassInfo.packedOverlay(), renderColor);
//        ExampleMod.LOGGER.debug("rendering {}", bone.name());
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<GeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        // todo: cache bones
        List<Optional<GeoBone>> bones = List.of(renderPassInfo.model().getBone("right_arm"), renderPassInfo.model().getBone("left_arm"));
        for (var bone : bones) {
            if (bone.isPresent())
                snapshots.get(bone.get()).skipRender(true);
        }
    }
}

