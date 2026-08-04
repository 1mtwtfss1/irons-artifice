package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.client.particle.ColorTransitionParticleOption;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.gun.MuzzleFlashSettings;
import io.redspace.irons_artifice.modifier.GunModifier;
import io.redspace.irons_artifice.modifier.on_hit_handlers.FreezePostHit;
import io.redspace.irons_artifice.modifier.on_hit_handlers.FrozenShrapnelOnHit;
import io.redspace.irons_artifice.registry.ParticleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.joml.Vector3f;

import java.util.List;

public final class FrozenJacketModifier implements GunModifier {
    private static final int MUZZLE_TINT = 0xA8E6FF;

    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.POST_HIT_EFFECTS).add(new FreezePostHit());
        components.getOrCreate(ShotComponents.ON_HIT).add(new FrozenShrapnelOnHit());
        components.getOrCreate(ShotComponents.PARTICLE_TRAIL).add(new ColorTransitionParticleOption(
                ParticleRegistry.BULLET_TRAIL.get(), FrozenShrapnelOnHit.TRAIL_COLOR_FROM, FrozenShrapnelOnHit.TRAIL_COLOR_TO, 1f, 0f, 1f, 1f, 0.5f, 0f, 0
        ));

        MuzzleFlashSettings flash = components.getOrDefault(ShotComponents.MUZZLE_FLASH);
        Vector3f color = ARGB.vector3fFromRGB24(MUZZLE_TINT);
        components.set(ShotComponents.MUZZLE_FLASH, flash.addTint(color));
    }

    @Override
    public List<Component> getDescriptionText() {
        return List.of(
                Component.translatable("irons_artifice.component_type.freeze_on_hit").withStyle(ChatFormatting.AQUA),
                Component.translatable("irons_artifice.modifier.frozen_jacket",
                        FrozenShrapnelOnHit.SHRAPNEL_COUNT,
                        (int) (FrozenShrapnelOnHit.DAMAGE_FRACTION * 100)).withStyle(ChatFormatting.AQUA)
        );
    }
}
