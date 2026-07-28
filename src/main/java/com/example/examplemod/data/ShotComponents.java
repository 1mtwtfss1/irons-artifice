package com.example.examplemod.data;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.GunShotSoundSettings;
import com.example.examplemod.gun.GunShotSoundStack;
import com.example.examplemod.gun.ImpactSoundStack;
import com.example.examplemod.gun.OnHitEffects;
import com.example.examplemod.recoil.RecoilProfile;
import com.example.examplemod.registry.SoundRegistry;
import net.minecraft.sounds.SoundEvents;

import java.util.Optional;

public final class ShotComponents {
    // Shot Mechanics
    public static final ComponentType<Value> PROJECTILE_COUNT = new ComponentType<>(ExampleMod.id("projectile_count"), () -> Value.of(1));
    public static final ComponentType<Value> SPREAD = new ComponentType<>(ExampleMod.id("spread"), () -> Value.of(0));
    public static final ComponentType<Value> FIRE_DELAY = new ComponentType<>(ExampleMod.id("fire_delay"), () -> Value.of(1));

    // Attributes
    public static final ComponentType<Value> DAMAGE = new ComponentType<>(ExampleMod.id("damage"), () -> Value.of(0));
    public static final ComponentType<Value> BULLET_SPEED = new ComponentType<>(ExampleMod.id("bullet_speed"), () -> Value.of(15));
    public static final ComponentType<Value> GRAVITY = new ComponentType<>(ExampleMod.id("gravity"), () -> Value.of(0.05));
    public static final ComponentType<Value> KNOCKBACK = new ComponentType<>(ExampleMod.id("knockback"), () -> Value.of(0));
    public static final ComponentType<Value> BULLET_DRAG = new ComponentType<>(ExampleMod.id("bullet_drag"), () -> Value.of(.98));
    public static final ComponentType<Value> BLOCK_DAMAGE_MULTIPLIER = new ComponentType<>(ExampleMod.id("block_damage_multiplier"), () -> Value.of(1));
    public static final ComponentType<Value> RELOAD_SPEED_MULTIPLIER = new ComponentType<>(ExampleMod.id("reload_speed_multiplier"), () -> Value.of(1));

    // Bullet Behavior
    public static final ComponentType<Value> PIERCING = new ComponentType<>(ExampleMod.id("piercing"), () -> Value.of(0));
    public static final ComponentType<Value> RICOCHET = new ComponentType<>(ExampleMod.id("ricochet"), () -> Value.of(0));
    public static final ComponentType<OnHitEffects> ON_HIT = new ComponentType<>(ExampleMod.id("on_hit"), OnHitEffects::new);
    public static final ComponentType<Boolean> BREAKS_BLOCKS = new ComponentType<>(ExampleMod.id("breaks_blocks"), () -> false);

    // Effects
    public static final ComponentType<RecoilProfile> CAMERA_RECOIL = new ComponentType<>(ExampleMod.id("camera_recoil"), () -> RecoilProfile.simple(10, 0));
    public static final ComponentType<Value> CAMERA_RECOIL_MULTIPLIER = new ComponentType<>(ExampleMod.id("camera_recoil_multiplier"), () -> Value.of(1));
    public static final ComponentType<Value> CHARACTER_RECOIL = new ComponentType<>(ExampleMod.id("character_recoil"), () -> Value.of(0));
    public static final ComponentType<GunShotSoundStack> GUNSHOT_SOUND = new ComponentType<>(ExampleMod.id("gunshot_sound"), () -> new GunShotSoundStack(
            GunShotSoundSettings.of(SoundEvents.FIREWORK_ROCKET_BLAST, 0.9f, 1.1f, -1f, 0f, 48f),
            PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f)));
    public static final ComponentType<ImpactSoundStack> IMPACT_SOUND = new ComponentType<>(ExampleMod.id("impact_sound"), () -> new ImpactSoundStack(
            Optional.of(PlayableSound.of(SoundRegistry.BULLET_IMPACT_GENERIC, 2f, .8f, 1.2f)), Optional.empty()
    ));
    public static final ComponentType<ParticleStack> PARTICLE_TRAIL = new ComponentType<>(ExampleMod.id("particle_trail"), ParticleStack::new);

}
