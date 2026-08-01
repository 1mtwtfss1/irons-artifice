package io.redspace.irons_artifice.data;

import io.redspace.irons_artifice.client.particle.ColorTransitionParticleOption;
import io.redspace.irons_artifice.registry.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import java.util.ArrayList;
import java.util.List;

public class ParticleStack {
    private static final List<ParticleOptions> EMPTY = List.of(new ColorTransitionParticleOption(
            ParticleRegistry.BULLET_TRAIL.get(), 0xffc600, 0x04f0b00, 1f, 0f, 1f, 1f, 0.5f, 0f, 0
    ));
    private final List<ParticleOptions> particles = new ArrayList<>();

    public void add(ParticleOptions options) {
        this.particles.add(options);
    }

    public List<ParticleOptions> getParticles() {
        return List.copyOf(particles.isEmpty() ? EMPTY : particles);
    }
}
