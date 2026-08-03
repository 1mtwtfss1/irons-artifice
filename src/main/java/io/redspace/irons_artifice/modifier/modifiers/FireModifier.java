package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.data.PlayableSound;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.data.ValueModifier;
import io.redspace.irons_artifice.modifier.GunModifier;
import io.redspace.irons_artifice.modifier.on_hit_handlers.IgnitePostHit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;

public final class FireModifier implements GunModifier {
    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.POST_HIT_EFFECTS).add(new IgnitePostHit());
        components.getOrCreate(ShotComponents.PARTICLE_TRAIL).add(ParticleTypes.FLAME);
        components.getOrCreate(ShotComponents.BULLET_DRAG).addModifier(new ValueModifier(-0.25, ValueModifier.Operation.ADD, ValueModifier.Type.BENEFICIAL));
        components.getOrCreate(ShotComponents.GUNSHOT_SOUND).addAccent(PlayableSound.of(PlayableSound.holder(SoundEvents.BLAZE_SHOOT), 4f, 1.2f, 1.4f));
    }
}
