package io.redspace.irons_artifice.client.particle;

import io.redspace.irons_artifice.utils.Utils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FireTrailParticle extends BulletTrailParticle {
    public FireTrailParticle(ClientLevel level, double x, double y, double z,
                             double xa, double ya, double za, SpriteSet spriteSet, ColorTransitionParticleOption particleOptions) {
        super(level, x, y, z, xa, ya, za, spriteSet, particleOptions);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age == 1 && this.level.getRandom().nextFloat() < 0.25f) {
            Vec3 motion = Utils.randomVec3(0.1).add(new Vec3(xd, yd, zd).scale(2));
            motion = motion.scale(0.25);
            this.level.addAlwaysVisibleParticle(
                    ParticleTypes.SMOKE, true,
                    this.x, this.y, this.z,
                    motion.x, motion.y, motion.z
            );
        }
    }

    public static class Provider implements ParticleProvider<ColorTransitionParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public @Nullable Particle createParticle(ColorTransitionParticleOption options, ClientLevel level,
                                                 double x, double y, double z,
                                                 double xa, double ya, double za, RandomSource random) {
            return new FireTrailParticle(level, x, y, z, xa, ya, za, this.sprite, options);
        }
    }
}
