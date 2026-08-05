package io.redspace.irons_artifice.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SplashParticle extends Particle {
    private final int color;

    public SplashParticle(ClientLevel level, double x, double y, double z,
                          double xa, double ya, double za, ColorParticleOption options) {
        super(level, x, y, z);
        this.setParticleSpeed(xa, ya, za);
        this.color = ARGB.opaque(ARGB.colorFromFloat(
                Math.max(options.getAlpha(), 1f),
                options.getRed(),
                options.getGreen(),
                options.getBlue()
        ));
        this.gravity = 1.5f;
        this.friction = 0.96f;
        this.hasPhysics = true;
        this.lifetime = 12 + this.random.nextInt(10);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.removed) {
            return;
        }
        this.level.addAlwaysVisibleParticle(
                ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, this.color),
                true,
                this.x, this.y, this.z,
                (this.random.nextDouble() - 0.5) * 0.04,
                this.random.nextDouble() * 0.04,
                (this.random.nextDouble() - 0.5) * 0.04
        );
    }

    @Override
    public @NonNull ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }

    public static class Provider implements ParticleProvider<ColorParticleOption> {

        public Provider() {
        }

        @Override
        public @Nullable Particle createParticle(ColorParticleOption options, ClientLevel level,
                                                 double x, double y, double z,
                                                 double xa, double ya, double za, RandomSource random) {
            return new SplashParticle(level, x, y, z, xa, ya, za, options);
        }
    }
}
