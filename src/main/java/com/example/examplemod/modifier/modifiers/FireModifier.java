package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.PlayableSound;
import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.gun.OnHitEffect;
import com.example.examplemod.modifier.GunModifier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;

public final class FireModifier implements GunModifier {
    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.ON_HIT).add(OnHitEffect.IGNITE);
        components.getOrCreate(ShotComponents.PARTICLE_TRAIL).add(ParticleTypes.FLAME);
        components.getOrCreate(ShotComponents.BULLET_DRAG).addModifier(new ValueModifier(-0.25, ValueModifier.Operation.ADD, ValueModifier.Type.BENEFICIAL));
        components.getOrCreate(ShotComponents.GUNSHOT_SOUND).addAccent(PlayableSound.of(SoundEvents.BLAZE_SHOOT, 4f, 1.2f, 1.4f));
    }
}
