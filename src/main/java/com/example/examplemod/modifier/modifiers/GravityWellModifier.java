package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.PlayableSound;
import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.gun.OnHitEffect;
import com.example.examplemod.modifier.GunModifier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.sounds.SoundEvents;

public final class GravityWellModifier implements GunModifier {
    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.ON_HIT).add(OnHitEffect.GRAVITY_WELL);
        components.getOrCreate(ShotComponents.GUNSHOT_SOUND).addAccent(PlayableSound.of(SoundEvents.BEACON_ACTIVATE, 4f, 1.6f, 1.8f));
        components.getOrCreate(ShotComponents.PARTICLE_TRAIL).add(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 0));
    }
}
