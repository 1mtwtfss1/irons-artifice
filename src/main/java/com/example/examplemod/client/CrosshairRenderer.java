package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.gun.ShotProfile;
import com.example.examplemod.item.GunItem;
import com.example.examplemod.item.GunplayManager;
import com.example.examplemod.menu.GunContainer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Matrix3x2fStack;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public final class CrosshairRenderer {
    private static final int COLOR = 0xFFFFFFFF;
    private static final int THICKNESS = 1;
    private static final int LENGTH = 3;
    private static final float GAP_BASE = 0.0F;
    private static final float GAP_PER_DEGREE = 1.90915243f; // magic number taking from in game measurements

    private static float crosshairGapCursor = 0.0F;
    private static float crosshairGapCursorO = 0.0F;

    private static int reloadAnimationDuration;
    private static int reloadAnimationTick;


    public static void triggerReloadAnimation(int reloadDuration) {
        reloadAnimationDuration = reloadDuration;
        reloadAnimationTick = reloadAnimationDuration;
    }

    public static boolean renderGunCrosshair(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.getCameraType().isFirstPerson()) {
            return false;
        }
        LocalPlayer player = minecraft.player;
        if (player == null || player.isSpectator()) {
            return false;
        }
        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof GunItem gunItem)) {
            return false;
        }

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        float degreesSpread = localCrosshairGap(partialTick);
        float gap = Math.max(GAP_BASE + degreesSpread * GAP_PER_DEGREE, 0);
        graphics.nextStratum();
        Matrix3x2fStack poseStack = graphics.pose();
        poseStack.pushMatrix();
        poseStack.translate(graphics.guiWidth() / 2 - 1, graphics.guiHeight() / 2);
        if (reloadAnimationTick > 0) {
            poseStack.translate(0.5f, 0.5f);
            float f = (reloadAnimationTick - partialTick) / reloadAnimationDuration;
            f = crosshairAnimationInterpolation(f);
            poseStack.rotate(f * 180 * Mth.DEG_TO_RAD);
            poseStack.translate(-0.5f, -0.5f);

        }
        drawCross(graphics, gap);
        poseStack.popMatrix();

        return true;
    }

    private static float crosshairAnimationInterpolation(float percent) {
//        return (float) Mth.smoothstep(percent);
        percent = 1 - percent;
        return 1 - (percent * percent * percent * percent * percent);
    }

    private static void updateCrosshairCursor() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof GunItem gunItem)) {
            return;
        }
        ShotProfile profile = GunplayManager.compose(gunItem.getGun(), new GunContainer(held), player, held, player.level());
        float spread = GunplayManager.getSpreadForEntity(profile, player);
        float recoilMagnitude = RecoilManager.localRecoilMagnitude();

        float targetDegrees = recoilMagnitude * 0.5f + spread;
        crosshairGapCursor = Mth.lerp(0.25f, crosshairGapCursor, targetDegrees);
        if (Math.abs(crosshairGapCursor) < 0.01) {
            crosshairGapCursor = 0;
        }
    }

    public static float localCrosshairGap(float partialTick) {
        return Mth.lerp(partialTick, crosshairGapCursorO, crosshairGapCursor);
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        crosshairGapCursorO = crosshairGapCursor;
        if (reloadAnimationTick > 0) {
            reloadAnimationTick--;
        }
        updateCrosshairCursor();
    }

    private static void drawCross(GuiGraphicsExtractor graphics, float gap) {
        Matrix3x2fStack poseStack = graphics.pose();

        // left prong;
        poseStack.pushMatrix();
        poseStack.translate(-gap - LENGTH, 0);
        graphics.fill(RenderPipelines.GUI_INVERT, 0, 0, LENGTH, THICKNESS, COLOR);
        poseStack.popMatrix();
        // right prong
        poseStack.pushMatrix();
        poseStack.translate(1 + gap, 0);
        graphics.fill(RenderPipelines.GUI_INVERT, 0, 0, LENGTH, THICKNESS, COLOR);
        poseStack.popMatrix();
        // top prong;
        poseStack.pushMatrix();
        poseStack.translate(0, -gap - LENGTH);
        graphics.fill(RenderPipelines.GUI_INVERT, 0, 0, THICKNESS, LENGTH, COLOR);
        poseStack.popMatrix();
        // down prong
        poseStack.pushMatrix();
        poseStack.translate(0, 1 + gap);
        graphics.fill(RenderPipelines.GUI_INVERT, 0, 0, THICKNESS, LENGTH, COLOR);
        poseStack.popMatrix();

    }
}
