package io.redspace.irons_artifice.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GunPreviewRenderer extends PictureInPictureRenderer<GunPreviewRenderState> {
    public GunPreviewRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public Class<GunPreviewRenderState> getRenderStateClass() {
        return GunPreviewRenderState.class;
    }

    @Override
    protected void renderToTexture(GunPreviewRenderState renderState, PoseStack poseStack) {
        poseStack.scale(1.0F, -1.0F, -1.0F);

        float boundsCenterX = (renderState.x0() + renderState.x1()) / 2.0F;
        float boundsCenterY = (renderState.y0() + renderState.y1()) / 2.0F;
        float scale = renderState.scale();
        poseStack.translate(
                (renderState.itemX() - boundsCenterX) / scale,
                (boundsCenterY - renderState.itemY()) / scale,
                0F
        );
        poseStack.mulPose(Axis.XP.rotationDegrees(15));
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot()));
        poseStack.translate(0, 0, 0);
        ItemStackRenderState item = renderState.item();
        if (item.usesBlockLight()) {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        } else {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        }

        FeatureRenderDispatcher featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        item.submit(poseStack, featureRenderDispatcher.getSubmitNodeStorage(), 15728880, OverlayTexture.NO_OVERLAY, 0);
        featureRenderDispatcher.renderAllFeatures();
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel() {
        return "gun_preview";
    }
}
