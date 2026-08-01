package com.example.examplemod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class MuzzleFlashParticle extends SingleQuadParticle {
    final SpriteSet sprites;

    final boolean mirrorHorizontal, mirrorVertical;

    public MuzzleFlashParticle(ClientLevel level, double x, double y, double z,
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
        this.mirrorHorizontal = level.getRandom().nextBoolean();
        this.mirrorVertical = level.getRandom().nextBoolean();
        this.roll = level.getRandom().nextInt(4) * Mth.HALF_PI; // 90 degrees
        this.oRoll = roll;
    }

    @Override
    protected float getU0() {
        return mirrorHorizontal ? super.getU1() : super.getU0();
    }

    @Override
    protected float getU1() {
        return mirrorHorizontal ? super.getU0() : super.getU1();
    }

    @Override
    protected float getV0() {
        return mirrorVertical ? super.getV1() : super.getV0();
    }

    @Override
    protected float getV1() {
        return mirrorVertical ? super.getV0() : super.getV1();
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(sprites);
    }

//    @Override
//    public float getQuadSize(float a) {
//        return Mth.lerp(Mth.clamp((age + a) / lifetime, 0, 1), 0.75f, 1.5f);
//    }

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
            return new MuzzleFlashParticle(level, x, y, z, xa, ya, za, this.sprite);
        }
    }
}
