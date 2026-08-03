package io.redspace.irons_artifice.gun;

import io.redspace.irons_artifice.registry.ParticleRegistry;
import net.minecraft.core.particles.SimpleParticleType;

public enum MuzzleFlashType {
    LARGE,
    TRIANGLE,
    SMALL_STAR,
    ;

    public SimpleParticleType particle() {
        return switch (this) {
            case LARGE -> ParticleRegistry.MUZZLE_FLASH_LARGE.get();
            case TRIANGLE -> ParticleRegistry.MUZZLE_FLASH_TRIANGLE.get();
            case SMALL_STAR -> ParticleRegistry.MUZZLE_FLASH_SMALL_STAR.get();
        };
    }
}
