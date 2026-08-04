package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.client.particle.ColorTransitionParticleOption;
import io.redspace.irons_artifice.data.PlayableSound;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.gun.MuzzleFlashSettings;
import io.redspace.irons_artifice.modifier.GunModifier;
import io.redspace.irons_artifice.modifier.on_hit_handlers.ChainLightningOnHit;
import io.redspace.irons_artifice.registry.ParticleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import org.joml.Vector3f;

import java.util.List;

public final class ChainLightningModifier implements GunModifier {
    private static final int LIGHTNING_COLOR = 0xcef8ff;
    private static final int coolColor = LIGHTNING_COLOR << 4;

    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.ON_HIT).add(new ChainLightningOnHit());
        components.getOrCreate(ShotComponents.PARTICLE_TRAIL).add(new ColorTransitionParticleOption(
                ParticleRegistry.BULLET_TRAIL.get(), LIGHTNING_COLOR, 0x00f8ff, 1f, 0f, 1f, 1f, 0.5f, 0f, 0
        ));
        components.getOrCreate(ShotComponents.GUNSHOT_SOUND).addAccent(PlayableSound.of(PlayableSound.holder(SoundEvents.GUARDIAN_ATTACK), 3f, 1.6f, 1.8f));

        MuzzleFlashSettings flash = components.getOrDefault(ShotComponents.MUZZLE_FLASH);
        Vector3f color = ARGB.vector3fFromRGB24(0x00f8ff);
        components.set(ShotComponents.MUZZLE_FLASH, flash.addTint(color));
    }

    @Override
    public List<Component> getDescriptionText() {
        return List.of(Component.translatable("irons_artifice.modifier.chain_lightning", ChainLightningOnHit.CHAIN_COUNT, (int) (ChainLightningOnHit.DAMAGE_MULTIPLIER * 100)).withStyle(ChatFormatting.AQUA));
    }
}
