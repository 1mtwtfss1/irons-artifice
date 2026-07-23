package com.example.examplemod.client;

import com.example.examplemod.network.ClientboundBulletImpactPacket;
import com.example.examplemod.network.ClientboundBulletTrailPacket;
import com.example.examplemod.network.ClientboundReloadCrosshairAnimationPacket;
import com.example.examplemod.registry.ParticleRegistry;
import com.example.examplemod.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
        int steps = (int) (from.distanceTo(to));
        if (steps <= 0) {
            return;
        }

        Vec3 dir = to.subtract(from).normalize();
        float speed = 0.03f;
        for (int i = 0; i < steps; i++) {
            Vec3 pos = from.lerp(to, (i + 1.0F) / steps);
            ParticleOptions particle = particles.get(i % particles.size());
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
        level.addAlwaysVisibleParticle(ParticleTypes.DRIPPING_LAVA, true, pos.x, pos.y, pos.z, 0, 0, 0);
        BlockPos impactedBlock = BlockPos.containing(pos.add(direction.scale(0.1)));
        BlockState blockState = level.getBlockState(impactedBlock);
        float particleSpeed = (10 + speed) * 0.02f;
        for (int i = 0; i < 5; i++) {
            Vec3 motion = new Vec3(level.getRandom().nextFloat() * 2 - 1, level.getRandom().nextFloat() * 2 - 1, level.getRandom().nextFloat() * 2 - 1).subtract(direction.scale(0.25)).normalize();
            motion = motion.scale(particleSpeed);
            level.addParticle(new BlockParticleOption(ParticleRegistry.BLOCK_IMPACT.get(), blockState), pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
        }
        for (int i = 0; i < 5; i++) {
            Vec3 motion = new Vec3(level.getRandom().nextFloat() * 2 - 1, level.getRandom().nextFloat() * 2 - 1, level.getRandom().nextFloat() * 2 - 1)
                    .add(reflected.scale(3)).normalize();
            motion = motion.scale(particleSpeed * 2);
            level.addParticle(new BlockParticleOption(ParticleRegistry.BLOCK_IMPACT.get(), blockState), pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
        }
    }

    public static void handleCrosshairAnimation(ClientboundReloadCrosshairAnimationPacket msg) {
        CrosshairRenderer.triggerReloadAnimation(msg.reloadDuration());
    }
}
