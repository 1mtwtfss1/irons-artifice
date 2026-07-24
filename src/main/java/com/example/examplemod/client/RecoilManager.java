package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.recoil.RecoilHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

/**
 * Client-side <b>predicted</b> camera recoil.
 *
 * <p>Recoil is a pure decaying aim offset applied only to the rendered camera angles (never to the
 * player's actual rotation). The server maintains the same offset via {@code ServerRecoil} and fires
 * bullets along it, so the crosshair and the shots agree. Both sides use {@link RecoilHelper} for the
 * impulse and decay, keeping the independent simulations aligned.
 */
@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public final class RecoilManager {
    private static final float EPSILON = 0.01F;
    // strength of 1 means cursor is just mirroring the real value perfectly.
    // the cursor (otherwise) acts as a smoother interpolation to the exact pitch/yaw, let snaps transition over multiple ticks instead of always 1.
    // snappy looks better though so
    private static final float CURSOR_STRENGTH = .5F;

    // Current recoil offset (degrees) and the previous-tick value for partial-tick interpolation.
    private static float pitch = 0.0F;
    private static float yaw = 0.0F;
    private static float pitchCursorO = 0.0F;
    private static float yawCursorO = 0.0F;
    private static float pitchCursor = 0.0F;
    private static float yawCursor = 0.0F;


    /**
     * Applies a recoil impulse from a local fire. {@code roundIndex} drives the yaw pattern.
     */
    public static void applyRecoil(float cameraRecoil, int roundIndex) {
        if (cameraRecoil <= 0.0F) {
            return;
        }

        // Permanent settle offset: shift the actual aim (negative pitch = look up). This syncs to
        // the server via normal movement packets, so the server's bullets inherit it automatically.
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.setXRot(player.getXRot() - RecoilHelper.permanentPitch(cameraRecoil));
            player.setYRot(player.getYRot() + RecoilHelper.permanentYaw(cameraRecoil, roundIndex));
        }

        // Transient kick: recovered over the next few ticks; mirrored by ServerRecoil for bullets.
        pitch += RecoilHelper.impulsePitch(cameraRecoil);
        yaw += RecoilHelper.impulseYaw(cameraRecoil, roundIndex);
    }

    @SubscribeEvent
    static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        float partialTick = (float) event.getPartialTick();
        float p = Mth.lerp(partialTick, pitchCursorO, pitchCursor);
        float y = Mth.lerp(partialTick, yawCursorO, yawCursor);
        if (p == 0.0F && y == 0.0F) {
            return;
        }
        // Positive pitch = look up (reduce camera pitch); yaw offset added directly.
        event.setPitch(event.getPitch() - p);
        event.setYaw(event.getYaw() + y);
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        pitchCursorO = pitchCursor;
        yawCursorO = yawCursor;
        pitch = decay(pitch);
        yaw = decay(yaw);
        updateCameraCursor();
    }

    private static float decay(float value) {
        float decayed = RecoilHelper.decay(value, 1);
        return Math.abs(decayed) < EPSILON ? 0.0F : decayed;
    }

    private static void updateCameraCursor() {
        pitchCursor = Mth.lerp(CURSOR_STRENGTH, pitchCursor, pitch);
        yawCursor = Mth.lerp(CURSOR_STRENGTH, yawCursor, yaw);
        if (Math.abs(pitchCursor) < EPSILON) {
            pitchCursor = 0;
        }
        if (Math.abs(yawCursor) < EPSILON) {
            yawCursor = 0;
        }
    }



    public static float localRecoilMagnitude() {
        return Mth.sqrt(yaw * yaw + pitch * pitch);
    }


}
