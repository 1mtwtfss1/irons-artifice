package io.redspace.irons_artifice.client.armor;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TransmogArmorRenderer<T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<T, R> {

    public TransmogArmorRenderer(GeoModel model) {
        super(model);
        this.asyncBones = new ArrayList<>();
        asyncBones.add(new AsyncBone(
                "armorLeggingTorsoLayer", EquipmentSlot.LEGS, ArmorSegment.CHEST, Vec3.ZERO));
        asyncBones.add(new AsyncBone(
                "armorTorsoExtensionRightLeg", EquipmentSlot.CHEST, ArmorSegment.RIGHT_LEG, new Vec3(2, 12, 0)));
        asyncBones.add(new AsyncBone(
                "armorTorsoExtensionLeftLeg", EquipmentSlot.CHEST, ArmorSegment.LEFT_LEG, new Vec3(-2, 12, 0)));
        this.capeBones = new ArrayList<>();
    }


    public record CapeBone(String name, Vec3 rotationMultipliers) {
    }

    private @NonNull ItemStack stackForSlot(R renderState, EquipmentSlot slot) {
        return switch (slot) {
            case FEET -> renderState.feetEquipment;
            case LEGS -> renderState.legsEquipment;
            case CHEST -> renderState.chestEquipment;
            case HEAD -> renderState.headEquipment;
            default -> ItemStack.EMPTY;
        };
    }

//    @Override
//    public List<ArmorSegment> getSegmentsForSlot(R renderState, EquipmentSlot slot) {
//        List<ArmorSegment> defaultSegments = super.getSegmentsForSlot(renderState, slot);
//        for(AsyncBone bone : asyncBones){
//            if(bone.itemSlot.equals(slot)){
//                defaultSegments.add(bone.segment);
//            }
//        }
//    }

    public Map<String, ArmorSegment> getBonesForEquipmentSlot(R renderState, EquipmentSlot slot) {
        HashMap<String, ArmorSegment> bones = new HashMap<>();
        // add default bones
        getSegmentsForSlot(renderState, slot).forEach(segment -> bones.put(getBoneNameForSegment(renderState, segment), segment));
//        bones.addAll(getSegmentsForSlot(renderState, slot).stream().map(segment -> getBoneNameForSegment(renderState, segment)).toList());
        // add custom bones
        asyncBones.forEach(bone -> {
            if (bone.itemSlot.equals(slot)) {
                bones.put(bone.boneName, bone.segment);
            }
        });
//        bones.addAll(asyncBones.stream().filter(bone -> bone.itemSlot.equals(slot)).map(bone -> bone.boneName).toList());
        return bones;
    }

    public Map<String, ArmorSegment> getAllBones(R renderState) {
        HashMap<String, ArmorSegment> bones = new HashMap<>();
        // add default bones
        for (var slot : List.of(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD)) {
            getSegmentsForSlot(renderState, slot).forEach(segment -> bones.put(getBoneNameForSegment(renderState, segment), segment));
        }
        // add custom bones
        asyncBones.forEach(bone -> {
            if (true/*bone.itemSlot.equals(slot)*/) {
                bones.put(bone.boneName, bone.segment);
            }
        });
        return bones;
    }

    @Override
    public void submitRenderTasks(RenderPassInfo<R> renderPassInfo, OrderedSubmitNodeCollector renderTasks, @Nullable RenderType renderType) {
        if (renderType == null)
            return;

        final int packedLight = renderPassInfo.packedLight();
        final int packedOverlay = renderPassInfo.packedOverlay();
        final int renderColor = renderPassInfo.renderColor();
        final R renderState = renderPassInfo.renderState();
        final EquipmentSlot slot = Objects.requireNonNull(renderState.getGeckolibData(CURRENT_SLOT));
        final BakedGeoModel model = renderPassInfo.model();

        if (model.isMissingno()) {
            submitMissingModelRender(renderPassInfo, renderTasks);
            return;
        }

        ItemStack currentStack = stackForSlot(renderPassInfo.renderState(), slot);
        final int dyeColor = -1;/*TransmogItemData.has(currentStack) ?
                TransmogItemData.get(currentStack).dyeColor() | 0xFF000000 :
                this.defaultDyeColor;*/
        renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, vertexConsumer) -> {
            final PoseStack poseStack = renderPassInfo.poseStack();

            poseStack.pushPose();
            poseStack.last().set(pose);
            Set<String> topLevelBones = getBonesForEquipmentSlot(renderState, slot).keySet();

            if (dyeable) {
                renderPassInfo.renderPosed(() -> {
                    for (String boneName : topLevelBones) {
                        var boneOpt = renderPassInfo.model().getBone(boneName);
                        if (boneOpt.isPresent()) {
                            var bone = boneOpt.get();
                            traverseAndSetDye(bone, true);
                            bone.positionAndRender(renderPassInfo, vertexConsumer, packedLight, packedOverlay, ARGB.multiply(renderColor, dyeColor));
                        }
                    }
                });
            }
            renderPassInfo.renderPosed(() -> {
                for (String boneName : topLevelBones) {
                    var boneOpt = renderPassInfo.model().getBone(boneName);
                    if (boneOpt.isPresent()) {
                        var bone = boneOpt.get();
                        traverseAndSetDye(bone, false);
                        bone.positionAndRender(renderPassInfo, vertexConsumer, packedLight, packedOverlay, renderColor);
                    }
                }
            });
            poseStack.popPose();
        });
        if (renderPassInfo.renderState().getOrDefaultGeckolibData(DataTickets.HAS_GLINT, false)) {
            renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), RenderTypes.armorEntityGlint(), (pose, vertexConsumer) -> {
                final PoseStack poseStack = renderPassInfo.poseStack();
                poseStack.pushPose();
                poseStack.last().set(pose);
                Set<String> topLevelBones = getBonesForEquipmentSlot(renderState, slot).keySet();
                renderPassInfo.renderPosed(() -> {
                    for (String boneName : topLevelBones) {
                        var boneOpt = renderPassInfo.model().getBone(boneName);
                        if (boneOpt.isPresent()) {
                            var bone = boneOpt.get();
                            traverseAndSetDye(bone, false);
                            bone.positionAndRender(renderPassInfo, vertexConsumer, packedLight, packedOverlay, renderColor);
                        }
                    }
                });
                poseStack.popPose();
            });
        }
    }

    private void setupBoneSnapshotsForChildren(BoneSnapshots snapshots, GeoBone parent) {
        for (GeoBone child : parent.children()) {
            snapshots.get(child);
            setupBoneSnapshotsForChildren(snapshots, child);
        }
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        final R renderState = renderPassInfo.renderState();
        final EquipmentSlot slot = Objects.requireNonNull(renderState.getGeckolibData(CURRENT_SLOT));
        final HumanoidModel baseModel = Objects.requireNonNull(renderState.getGeckolibData(BASE_MODEL));
        final List<ArmorSegment> segments = List.of(
                ArmorSegment.HEAD,
                ArmorSegment.CHEST,
                ArmorSegment.LEFT_ARM,
                ArmorSegment.RIGHT_ARM,
                ArmorSegment.LEFT_LEG,
                ArmorSegment.RIGHT_LEG,
                ArmorSegment.LEFT_FOOT,
                ArmorSegment.RIGHT_FOOT
        );
        baseModel.setupAnim(renderState);
        var topLevelBones = getAllBones(renderState);//getBonesForEquipmentSlot(renderState, slot);
        for (var pair : topLevelBones.entrySet()) {
            String boneName = pair.getKey();
            ArmorSegment segment = pair.getValue();
            var snapshotOpt = snapshots.get(boneName);
            if (snapshotOpt.isPresent()) {
                BoneSnapshot snapshot = snapshotOpt.get();
                final ModelPart modelPart = segment.modelPartGetter.apply(baseModel);
                final Vector3f bonePos = segment.modelPartMatcher.apply(new Vector3f(modelPart.x, modelPart.y, modelPart.z));
                snapshot.setRotX(-modelPart.xRot)
                        .setRotY(-modelPart.yRot)
                        .setRotZ(modelPart.zRot)
                        .setTranslateX(bonePos.x)
                        .setTranslateY(bonePos.y)
                        .setTranslateZ(bonePos.z);
                if (dyeable) {
                    // dyeable armor needs per-bone snapshot context
                    renderPassInfo.model().getBone(boneName).ifPresent(bone -> setupBoneSnapshotsForChildren(snapshots, bone));
                }
            }
        }
        // todo: cape or ilib
//        CapeData capeData = ((ICapeDataProvider) renderState).irons_lib$getCapeData();
////        Vec3 capeRotation = capeData.calculateRotationsDegrees(renderState.partialTick);
//        for (CapeBone capeBone : capeBones) {
//            var snapshotOpt = snapshots.get(capeBone.name());
//            if (snapshotOpt.isPresent()) {
//                var snapshot = snapshotOpt.get();
//                snapshot.setRotX((float) (capeData.capeLean * -Mth.DEG_TO_RAD * capeBone.rotationMultipliers.x))
//                        .setRotY((float) (capeData.capeFlap * -Mth.DEG_TO_RAD * capeBone.rotationMultipliers.y))
//                        .setRotZ((float) (capeData.capeLean2 * Mth.DEG_TO_RAD * capeBone.rotationMultipliers.z));
//            }
//        }
    }

    private void traverseAndSetDye(GeoBone bone, boolean renderDyed) {
        if (bone.frameSnapshot != null) {
            bone.frameSnapshot.skipRender(bone.name().startsWith("dye") ^ renderDyed);
        }
        for (GeoBone child : bone.children()) {
            traverseAndSetDye(child, renderDyed);
        }
    }

    public static class AsyncBone {
        private final String boneName;
        private final EquipmentSlot itemSlot;
        private final ArmorSegment segment;

        public AsyncBone(String boneName, EquipmentSlot itemSlot, ArmorSegment segment, Vec3 partOffset) {
            this.boneName = boneName;
            this.itemSlot = itemSlot;
            this.segment = segment;
        }
    }

    protected final ArrayList<AsyncBone> asyncBones;
    protected final ArrayList<CapeBone> capeBones;
    private boolean hideHat, hideJacket, dyeable;
    private int defaultDyeColor = 0xFFFFFFF;

    public boolean hideHat() {
        return hideHat;
    }

    public boolean hideJacket() {
        return hideJacket;
    }

    public TransmogArmorRenderer<T, R> setHideHat() {
        this.hideHat = true;
        return this;
    }

    public TransmogArmorRenderer<T, R> setHideJacket() {
        this.hideJacket = true;
        return this;
    }

    public TransmogArmorRenderer<T, R> setDyeable() {
        this.dyeable = true;
        return this;
    }

    public TransmogArmorRenderer<T, R> setDyeable(int defaultDyeColor) {
        this.dyeable = true;
        this.defaultDyeColor = defaultDyeColor;
        return this;
    }

    public TransmogArmorRenderer<T, R> withCapeBone(String bone) {
        this.capeBones.add(new CapeBone(bone, new Vec3(0.5, 0, 0)));
        return this;
    }

    public TransmogArmorRenderer<T, R> withCapeBone(CapeBone... bones) {
        for (CapeBone bone : bones) {
            this.capeBones.add(bone);
        }
        return this;
    }
}