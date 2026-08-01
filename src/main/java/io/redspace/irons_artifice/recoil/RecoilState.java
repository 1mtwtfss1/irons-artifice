package io.redspace.irons_artifice.recoil;

import io.redspace.irons_artifice.gun.ShotProfile;
import io.redspace.irons_artifice.registry.DataAttachmentRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;

/**
 * Server-side recoil accumulator stored as a data attachment on the shooter.
 *
 * <p>Holds the current aim offsetSeconds plus the game-tick at which it was last updated, enabling lazy
 * decay: the offsetSeconds is decayed by elapsed ticks only when read at fire time, so no per-tick handler
 * is needed.
 *
 * @param pitch current vertical offsetSeconds in degrees (positive = up)
 * @param yaw   current horizontal offsetSeconds in degrees
 * @param tick  game time of the last update
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
     * Adds this shot's recoil impulse on top of the current (decayed) offsetSeconds and stores it.
     *
     * @param roundIndex the shot's index within the magazine (drives the deterministic yaw pattern)
     */
    public static void addImpulse(ServerPlayer player, long now, ShotProfile shotProfile) {
        RecoilState decayed = current(player, now);
        Vec2 recoil = RecoilHelper.calculateCameraRecoil(shotProfile);
        RecoilState next = new RecoilState(
                decayed.pitch() + recoil.x,
                decayed.yaw() + recoil.y,
                now);
        player.setData(DataAttachmentRegistry.RECOIL, next);
    }
}
