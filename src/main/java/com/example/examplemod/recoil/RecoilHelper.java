package com.example.examplemod.recoil;

public final class RecoilHelper {
    /** Fraction of the recoil offset retained each tick during recovery (lower = snappier). */
    public static final float RETENTION = 0.75F;

    /**
     * Fraction of each shot's kick applied <b>permanently</b> to the player's actual aim (a settle
     * offset the player must correct with the mouse); the remainder is the transient, recovered
     * offset. Applied client-side as a physical rotation change, which syncs to the server, so the
     * server's bullets inherit it via the player's rotation. The transient remainder is tracked
     * separately (client camera + {@code ServerRecoil}).
     */
    public static final float PERMANENT_FRACTION = 0.15F;

    /** Horizontal kick as a fraction of the vertical kick. */
    private static final float YAW_FRACTION = 0.33F;
    /** Frequency of the deterministic horizontal spray pattern, in radians per round. */
    private static final double YAW_PATTERN_FREQ = 1.7;

    /** Full vertical kick (degrees, look-up) for one shot, before the permanent/transient split. */
    private static float rawPitch(float cameraRecoil) {
        return cameraRecoil;
    }

    /**
     * Full horizontal kick (degrees) for one shot before the split. Deterministic zig-zag keyed on
     * the round index so the pattern is learnable and identical on client and server.
     */
    private static float rawYaw(float cameraRecoil, int roundIndex) {
        return (float) Math.sin(roundIndex * YAW_PATTERN_FREQ) * cameraRecoil * YAW_FRACTION;
    }

    /** Permanent (non-recovered) vertical kick applied to the player's actual aim. */
    public static float permanentPitch(float cameraRecoil) {
        return rawPitch(cameraRecoil) * PERMANENT_FRACTION;
    }

    /** Permanent (non-recovered) horizontal kick applied to the player's actual aim. */
    public static float permanentYaw(float cameraRecoil, int roundIndex) {
        return rawYaw(cameraRecoil, roundIndex) * PERMANENT_FRACTION;
    }

    /** Transient vertical kick added to the recovered offset (client camera + server bullets). */
    public static float impulsePitch(float cameraRecoil) {
        return rawPitch(cameraRecoil) * (1.0F - PERMANENT_FRACTION);
    }

    /** Transient horizontal kick added to the recovered offset (client camera + server bullets). */
    public static float impulseYaw(float cameraRecoil, int roundIndex) {
        return rawYaw(cameraRecoil, roundIndex) * (1.0F - PERMANENT_FRACTION);
    }

    /** Decays a recoil component over {@code ticks} elapsed ticks. */
    public static float decay(float value, int ticks) {
        if (ticks <= 0) {
            return value;
        }
        return value * (float) Math.pow(RETENTION, ticks);
    }
}
