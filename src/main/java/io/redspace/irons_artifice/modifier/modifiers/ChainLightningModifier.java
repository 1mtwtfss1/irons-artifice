package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.data.PlayableSound;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.modifier.GunModifier;
import io.redspace.irons_artifice.modifier.on_hit_handlers.ChainLightningOnHit;
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
