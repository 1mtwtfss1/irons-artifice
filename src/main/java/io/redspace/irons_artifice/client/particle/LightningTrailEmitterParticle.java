package io.redspace.irons_artifice.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;

public class LightningTrailEmitterParticle extends BulletTrailParticle {
    private static final float EMIT_CHANCE = 0.1f;

    public LightningTrailEmitterParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za, SpriteSet spriteSet, ColorTransitionParticleOption particleOptions) {
        super(level, x, y, z, xa, ya, za, spriteSet, particleOptions);
        if(level.getRandom().nextFloat() < EMIT_CHANCE){
            emitLightningTrail(level, x, y, z, xa, ya, za);
        }
    }

    private void emitLightningTrail(ClientLevel level, double x, double y, double z, double xa, double ya, double za) {

    }
}
