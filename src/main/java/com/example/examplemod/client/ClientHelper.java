package com.example.examplemod.client;

import com.example.examplemod.client.particle.ColorTransitionParticleOption;
import com.example.examplemod.client.particle.ITrailParticle;
import com.example.examplemod.entity.Bullet;
import com.example.examplemod.item.GunItem;
import com.example.examplemod.network.ClientboundBulletImpactPacket;
import com.example.examplemod.network.ClientboundBulletTrailPacket;
import com.example.examplemod.network.ClientboundGunAnimationPacket;
import com.example.examplemod.network.ClientboundGunshotSoundPacket;
import com.example.examplemod.network.ClientboundReloadCrosshairAnimationPacket;
import com.example.examplemod.registry.ParticleRegistry;
import com.example.examplemod.utils.Utils;
import com.geckolib.animation.AnimationController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class ClientHelper {

    public static void handleBulletTrail(ClientboundBulletTrailPacket msg) {
        ClientLevel level = Minecraft.getInstance().level;
        List<ParticleOptions> particles = msg.particles();
        if (level == null || particles.isEmpty()) {
            return;
        }

        Vec3 from = msg.from();
        Vec3 to = msg.to();
        double distance = from.distanceTo(to);
        int steps = (int) (distance * Bullet.TRAIL_DENSITY);
        if (steps <= 0) {
            return;
        }

        Vec3 dir = to.subtract(from).normalize();
        float speed = 12 / ((float) Bullet.TRAIL_DENSITY * 15f);
        for (int i = 0; i < steps; i++) {
            float f = (i + 1.0F) / steps;
            Vec3 pos = from.lerp(to, f);
            ParticleOptions particle = particles.get(i % particles.size());
            if (particle.getType() instanceof ITrailParticle trailParticle) {
                particle = trailParticle.applyTrailInterpolation(particle, (1 - f));
            }
            level.addAlwaysVisibleParticle(particle, true,
                    pos.x, pos.y, pos.z,
                    dir.x * speed, dir.y * speed, dir.z * speed);
        }
    }

    public static void handleBulletImpact(ClientboundBulletImpactPacket msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Vec3 direction = msg.deltaMovement().normalize();
        Vec3 pos = msg.position().subtract(direction.scale(0.05));
        float speed = (float) msg.deltaMovement().length();
        Vec3 reflected = Utils.reflect(direction, msg.normal());
        level.addAlwaysVisibleParticle(new ColorTransitionParticleOption(
                ParticleRegistry.BULLET_IMPACT.get(), 0x862900, 0x0F0600, 0.125f, 0f, 1f, 0.75f, 0.35f, 0.35f, 0
        ), true, pos.x, pos.y, pos.z, 0, 0, 0);
        BlockPos impactedBlock = BlockPos.containing(pos.add(direction.scale(0.1)));
        BlockState blockState = level.getBlockState(impactedBlock);
        float particleSpeed = (10 + speed) * 0.02f;
        for (int i = 0; i < 5; i++) {
            Vec3 motion = new Vec3(level.getRandom().nextFloat() * 2 - 1, level.getRandom().nextFloat() * 2 - 1, level.getRandom().nextFloat() * 2 - 1).subtract(direction.scale(0.25)).normalize();
            motion = motion.scale(particleSpeed);
            level.addParticle(new BlockParticleOption(ParticleRegistry.BLOCK_IMPACT.get(), blockState), pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
            motion = motion.scale(0.25);
            level.addParticle(new BlockParticleOption(ParticleRegistry.BLOCK_DUST.get(), blockState), pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
        }
        for (int i = 0; i < 5; i++) {
            Vec3 motion = new Vec3(level.getRandom().nextFloat() * 2 - 1, level.getRandom().nextFloat() * 2 - 1, level.getRandom().nextFloat() * 2 - 1)
                    .add(reflected.scale(3)).normalize();
            motion = motion.scale(particleSpeed * 2);
            level.addParticle(new BlockParticleOption(ParticleRegistry.BLOCK_IMPACT.get(), blockState), pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
        }
    }

    public static void handleGunAnimationPacket(ClientboundGunAnimationPacket msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(msg.entityId());
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        ItemStack stack = livingEntity.getItemInHand(msg.interactionHand());
        if (!(stack.getItem() instanceof GunItem gun)) {
            return;
        }
        playClientGunAnimation(gun, msg.instanceId(), msg.animName(), msg.speed(), msg.offsetSeconds());
    }

    public static void playClientGunAnimation(GunItem gun, long instanceId, String animName, double speed, double offsetSeconds) {
        AnimationController<?> controller = gun.getAnimatableInstanceCache().getManagerForId(instanceId).getAnimationControllers().get(GunItem.TRIGGERED_ANIMATION_CONTROLLER);
        if (controller == null) {
            return;
        }
        controller.triggerAnimation(animName);
        controller.setAnimationSpeed(speed);
        // IMPORTANT: set even if zero (controller workaround doesn't handle context-free transitions)
        controller.setTimelineTime(offsetSeconds);
    }

    public static void handleCrosshairAnimation(ClientboundReloadCrosshairAnimationPacket msg) {
        CrosshairRenderer.triggerReloadAnimation(msg.reloadDuration());
    }

    public static void handleGunshotSound(ClientboundGunshotSoundPacket msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Vec3 pos = new Vec3(msg.x(), msg.y(), msg.z());
        RandomSource random = RandomSource.create();
        for (GunShotSoundSettings settings : msg.sounds()) {
            var instance = new GunShotSoundInstance(settings, msg.source(), random, pos);
            if (instance.getDelay() > 0) {
                Minecraft.getInstance().getSoundManager().playDelayed(instance, instance.getDelay());
            } else {
                Minecraft.getInstance().getSoundManager().play(instance);
            }
        }
    }
}
