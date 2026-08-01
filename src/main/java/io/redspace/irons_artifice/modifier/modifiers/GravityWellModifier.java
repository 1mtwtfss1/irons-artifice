package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.data.PlayableSound;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.gun.OnHitEffect;
import io.redspace.irons_artifice.modifier.GunModifier;
import io.redspace.irons_artifice.modifier.on_hit_handlers.GravityWellOnHit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.sounds.SoundEvents;

public final class GravityWellModifier implements GunModifier {
    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.ON_HIT).add(new GravityWellOnHit());
        components.getOrCreate(ShotComponents.GUNSHOT_SOUND).addAccent(PlayableSound.of(PlayableSound.holder(SoundEvents.BEACON_ACTIVATE), 4f, 1.6f, 1.8f));
        components.getOrCreate(ShotComponents.PARTICLE_TRAIL).add(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 0));
    }
}
