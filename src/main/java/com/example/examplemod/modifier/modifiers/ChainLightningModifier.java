package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.PlayableSound;
import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.gun.OnHitEffect;
import com.example.examplemod.modifier.GunModifier;
import com.example.examplemod.modifier.on_hit_handlers.ChainLightningOnHit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;

public final class ChainLightningModifier implements GunModifier {
    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.ON_HIT).add(new ChainLightningOnHit());
        components.getOrCreate(ShotComponents.PARTICLE_TRAIL).add(ParticleTypes.SCULK_CHARGE_POP);
        components.getOrCreate(ShotComponents.GUNSHOT_SOUND).addAccent(PlayableSound.of(PlayableSound.holder(SoundEvents.GUARDIAN_ATTACK), 3f, 1.6f, 1.8f));
    }
}
