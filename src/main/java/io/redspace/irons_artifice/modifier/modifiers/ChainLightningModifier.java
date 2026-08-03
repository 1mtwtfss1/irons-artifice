package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.data.PlayableSound;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.gun.MuzzleFlashSettings;
import io.redspace.irons_artifice.modifier.GunModifier;
import io.redspace.irons_artifice.modifier.on_hit_handlers.ChainLightningOnHit;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.List;

public final class ChainLightningModifier implements GunModifier {
    //    static int test = 0;
    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.ON_HIT).add(new ChainLightningOnHit());
        components.getOrCreate(ShotComponents.PARTICLE_TRAIL).add(ParticleTypes.SCULK_CHARGE_POP);
        components.getOrCreate(ShotComponents.GUNSHOT_SOUND).addAccent(PlayableSound.of(PlayableSound.holder(SoundEvents.GUARDIAN_ATTACK), 3f, 1.6f, 1.8f));

        MuzzleFlashSettings flash = components.getOrDefault(ShotComponents.MUZZLE_FLASH);
//        test++;
//        float r = Mth.sin(test * Mth.DEG_TO_RAD);
//        float g = Mth.sin((test * 2) * Mth.DEG_TO_RAD);
//        float b = Mth.sin((test * 3) * Mth.DEG_TO_RAD);
        components.set(ShotComponents.MUZZLE_FLASH, flash.withTint(0.5f, 0.9f, 1f));
//        components.set(ShotComponents.MUZZLE_FLASH, flash.withTint(r * r, g * g, b * b));
    }

    @Override
    public List<Component> getDescriptionText() {
        return List.of(Component.translatable("irons_artifice.modifier.chain_lightning", ChainLightningOnHit.CHAIN_COUNT, (int) (ChainLightningOnHit.DAMAGE_MULTIPLIER * 100)).withStyle(ChatFormatting.AQUA));
    }
}
