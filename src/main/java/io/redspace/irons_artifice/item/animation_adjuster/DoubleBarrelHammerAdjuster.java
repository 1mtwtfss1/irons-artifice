package io.redspace.irons_artifice.item.animation_adjuster;

import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import io.redspace.irons_artifice.api.GunBones;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.item.MagazineContents;

import java.util.Optional;

public final class DoubleBarrelHammerAdjuster implements AnimationAdjuster {
    @Override
    public void adjust(RenderPassInfo<GeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        MagazineContents magazineContents = renderPassInfo.getGeckolibData(GunItem.MAGAZINE_ANIMATION_TICKET);
        double reloadProgress = renderPassInfo.getOrDefaultGeckolibData(GunItem.RELOAD_PROGRESS_SECONDS_TICKET, 0.0);
        Optional<BoneSnapshot> leftOpt = snapshots.get(GunBones.HAMMER_LEFT);
        Optional<BoneSnapshot> rightOpt = snapshots.get(GunBones.HAMMER_RIGHT);
        if (leftOpt.isEmpty() || rightOpt.isEmpty() || magazineContents == null) {
            return;
        } else if (reloadProgress <= 1.17) {
            if (magazineContents.count() <= 1) {
                leftOpt.get().setRotation(0, 0, 0);
            }
            if (magazineContents.isEmpty()) {
                rightOpt.get().setRotation(0, 0, 0);
            }
        }
    }
}
