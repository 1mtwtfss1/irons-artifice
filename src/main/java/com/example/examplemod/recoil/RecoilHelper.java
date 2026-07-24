package com.example.examplemod.recoil;

import com.example.examplemod.gun.ShotProfile;
import com.example.examplemod.item.GunItem;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public final class RecoilHelper {
    /**
     * Fraction of the recoil offset retained each tick during recovery (lower = snappier).
     */
    public static final float RETENTION = 0.75F;

    public static final float PERMANENT_FRACTION = 0.15F;

    /**
     * Deterministic step based on the round index being fired out of the magazine <br>
     * Used for client-server agreement, and consistent recoil patterns
     */
    public static int getBulletIndex(ShotProfile shotProfile){
        return shotProfile.gun().magazineCapacity() - GunItem.getMagazine(shotProfile.itemStack()).count();
    }
    /**
     * @return pitch, yaw in degrees
     */
    public static Vec2 calculateFullRecoil(RecoilProfile profile, int index) {
        float pitch = profile.magnitude();
        float yaw = profile.magnitude() * profile.horizontalRatio() *
                Mth.sin((index + profile.seed()) * profile.horizontalPatternFrequency());
        return new Vec2(pitch, yaw);
    }
    /**
     * Recoil applied permanently to character's rotation
     * @return pitch, yaw in degrees
     */
    public static Vec2 calculatePermanentRecoil(RecoilProfile profile, int index) {
        Vec2 fullRecoil = calculateFullRecoil(profile, index);
        return fullRecoil.scale(PERMANENT_FRACTION);
    }
    /**
     * Recoil applied temporarily to character's camera and aim
     * @return pitch, yaw in degrees
     */
    public static Vec2 calculateCameraRecoil(RecoilProfile profile, int index) {
        Vec2 fullRecoil = calculateFullRecoil(profile, index);
        return fullRecoil.scale(1 - PERMANENT_FRACTION);
    }

    /**
     * Decays a recoil component over {@code ticks} elapsed ticks.
     */
    public static float decay(float value, int ticks) {
        if (ticks <= 0) {
            return value;
        }
        return value * (float) Math.pow(RETENTION, ticks);
    }
}
