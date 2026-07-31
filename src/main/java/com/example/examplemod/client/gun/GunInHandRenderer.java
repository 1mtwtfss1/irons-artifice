package com.example.examplemod.client.gun;

import com.example.examplemod.item.GunItem;
import com.example.examplemod.item.MagazineContents;
import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class GunInHandRenderer extends GeoItemRenderer<GunItem> {


    public GunInHandRenderer(GeoModel<GunItem> model) {
        super(model);
    }

    @Override
    public void preRenderPass(@NonNull RenderPassInfo<GeoRenderState> renderPassInfo, @NonNull SubmitNodeCollector renderTasks) {
        super.preRenderPass(renderPassInfo, renderTasks);
        // Use Marker Bones "right_arm" and "left_arm" to render the player's hands in first person
        if (!isFirstPersonPerspective(renderPassInfo.renderState())) {
            return;
        }
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        final GeoRenderState renderState = renderPassInfo.renderState();
        Identifier skinTexture = player.getSkin().body().texturePath();
        final RenderType renderType = getRenderType(renderState, skinTexture);
        if (!(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player) instanceof AvatarRenderer renderer)) {
            return;
        }
        // fixme: does this handle jacket layer?
        renderPassInfo.model().getBone("right_arm").ifPresent(bone ->
                renderPassInfo.addPerBoneRender(bone, (renderPassInfo1, bone1, renderTasks1) -> {
                            var modelPart = ((PlayerModel) renderer.getModel()).rightArm;
                            renderFirstPersonHand(renderTasks, renderType, modelPart, renderPassInfo.poseStack().last(), renderPassInfo);
                        }
                )
        );
        renderPassInfo.model().getBone("left_arm").ifPresent(bone ->
                renderPassInfo.addPerBoneRender(bone, (renderPassInfo1, bone1, renderTasks1) -> {
                            var modelPart = ((PlayerModel) renderer.getModel()).leftArm;
                            renderFirstPersonHand(renderTasks, renderType, modelPart, renderPassInfo.poseStack().last(), renderPassInfo);
                        }
                )
        );
    }

    @Override
    @SuppressWarnings("all")
    public void captureDefaultRenderState(GunItem animatable, RenderData renderData, GeoRenderState renderState, float partialTick) {
        super.captureDefaultRenderState(animatable, renderData, renderState, partialTick);
        if (MagazineContents.has(renderData.itemStack())) {
            renderState.addGeckolibData(GunItem.MAGAZINE_ANIMATION_TICKET, MagazineContents.get(renderData.itemStack()));
        }
    }

    @Override
    public void adjustModelBonesForRender(@NonNull RenderPassInfo<GeoRenderState> renderPassInfo, @NonNull BoneSnapshots snapshots) {
        super.adjustModelBonesForRender(renderPassInfo, snapshots);
        normalizeThirdPersonGunAnimations(renderPassInfo, snapshots);
    }

    private void normalizeThirdPersonGunAnimations(@NonNull RenderPassInfo<GeoRenderState> renderPassInfo, @NonNull BoneSnapshots snapshots) {
        // Root bone tends to contain large animations that only make sense in first person.
        // Cancel them out in third person viewers
        if (isFirstPersonPerspective(renderPassInfo.renderState())) {
            return;
        }
        Optional<BoneSnapshot> rootOpt = snapshots.get("root");
        if (rootOpt.isEmpty()) {
            return;
        }
        BoneSnapshot root = rootOpt.get();
        root.setTranslation(0, 0, 0);
        root.setRotation(0, 0, 0);
    }

    private void renderFirstPersonHand(SubmitNodeCollector renderTasks, RenderType renderType, ModelPart modelPart, PoseStack.Pose pose, RenderPassInfo<GeoRenderState> renderPassInfo) {
        modelPart.x = 0;
        modelPart.y = 0;
        modelPart.z = 0;
        modelPart.xRot = 0;
        modelPart.yRot = 0;
        modelPart.zRot = 0;
        final PoseStack poseStack = new PoseStack();
        poseStack.last().set(pose);
        poseStack.scale(-1, -1, 1);
        // no clue where these numbers come from (manually lined up from block bench)
        poseStack.translate(1 / 16f, -10 / 16f, 0 / 16f);
        renderTasks.submitModelPart(
                modelPart,
                poseStack,
                renderType,
                renderPassInfo.packedLight(),
                OverlayTexture.NO_OVERLAY,
                null
        );
    }

    private boolean isFirstPersonPerspective(GeoRenderState renderState) {
        var perspective = renderState.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE);
        return perspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || perspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
    }
}

