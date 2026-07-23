package com.example.examplemod.client;

import com.example.examplemod.network.ClientboundBulletTrailPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
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
}
