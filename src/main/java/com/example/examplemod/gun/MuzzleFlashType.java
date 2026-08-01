package com.example.examplemod.gun;

import com.example.examplemod.registry.ParticleRegistry;
import net.minecraft.core.particles.SimpleParticleType;

public enum MuzzleFlashType {
    LARGE,
    TRIANGLE;

    public SimpleParticleType particle() {
        return switch (this) {
            case LARGE -> ParticleRegistry.MUZZLE_FLASH_LARGE.get();
            case TRIANGLE -> ParticleRegistry.MUZZLE_FLASH_TRIANGLE.get();
        };
    }
}
