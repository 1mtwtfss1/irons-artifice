package com.example.examplemod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class MuzzleFlashLargeParticle extends SingleQuadParticle {
    final SpriteSet sprites;

    public MuzzleFlashLargeParticle(ClientLevel level, double x, double y, double z,
                                    double xa, double ya, double za, SpriteSet sprites) {
        super(level, x, y, z, xa, ya, za, sprites.first());
        setSpriteFromAge(sprites);
        this.sprites = sprites;
        this.lifetime = 3;
        this.xd = xa;
        this.yd = ya;
        this.zd = za;
        this.quadSize = 1;
        this.rCol = 1;
        this.gCol = 0.77f;
        this.bCol = 0f;
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(sprites);
    }

    @Override
    protected int getLightCoords(float a) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level,
                                                 double x, double y, double z,
                                                 double xa, double ya, double za, RandomSource random) {
            return new MuzzleFlashLargeParticle(level, x, y, z, xa, ya, za, this.sprite);
        }
    }
}
