package com.example.examplemod.recoil;

import com.example.examplemod.registry.DataAttachmentRegistry;
import net.minecraft.server.level.ServerPlayer;

/**
 * Server-side recoil accumulator stored as a data attachment on the shooter.
 *
 * <p>Holds the current aim offset plus the game-tick at which it was last updated, enabling lazy
 * decay: the offset is decayed by elapsed ticks only when read at fire time, so no per-tick handler
 * is needed.
 *
 * @param pitch    current vertical offset in degrees (positive = look-up)
 * @param yaw      current horizontal offset in degrees
 * @param tick     game time of the last update
 */
public record RecoilState(float pitch, float yaw, long tick) {
    public static final RecoilState NONE = new RecoilState(0.0F, 0.0F, 0L);

    public static RecoilState current(ServerPlayer player, long now) {
        RecoilState state = player.getData(DataAttachmentRegistry.RECOIL);
        int elapsed = (int) Math.max(0L, now - state.tick());
        if (elapsed == 0) {
            return state;
        }
        return new RecoilState(
                RecoilHelper.decay(state.pitch(), elapsed),
                RecoilHelper.decay(state.yaw(), elapsed),
                now);
    }

    /**
     * Adds this shot's recoil impulse on top of the current (decayed) offset and stores it.
     *
     * @param roundIndex the shot's index within the magazine (drives the deterministic yaw pattern)
     */
    public static void addImpulse(ServerPlayer player, long now, float cameraRecoil, int roundIndex) {
        RecoilState decayed = current(player, now);
        RecoilState next = new RecoilState(
                decayed.pitch() + RecoilHelper.impulsePitch(cameraRecoil),
                decayed.yaw() + RecoilHelper.impulseYaw(cameraRecoil, roundIndex),
                now);
        player.setData(DataAttachmentRegistry.RECOIL, next);
    }
}
