package com.example.examplemod.data;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import java.util.ArrayList;
import java.util.List;

public class ParticleStack {
    private static final List<ParticleOptions> EMPTY = List.of(ParticleTypes.SMOKE);
    private final List<ParticleOptions> particles = new ArrayList<>();

    public void add(ParticleOptions options) {
        this.particles.add(options);
    }

    public List<ParticleOptions> getParticles() {
        return List.copyOf(particles.isEmpty() ? EMPTY : particles);
    }
}
