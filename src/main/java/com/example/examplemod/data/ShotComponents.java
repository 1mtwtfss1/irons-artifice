package com.example.examplemod.data;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.gun.GunShotSoundStack;
import com.example.examplemod.gun.OnHitEffects;
import com.example.examplemod.recoil.RecoilProfile;
import net.minecraft.sounds.SoundEvents;

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

    // Bullet Behavior
    public static final ComponentType<Value> PIERCING = new ComponentType<>(ExampleMod.id("piercing"), () -> Value.of(0));
    public static final ComponentType<Value> RICOCHET = new ComponentType<>(ExampleMod.id("ricochet"), () -> Value.of(0));
    public static final ComponentType<OnHitEffects> ON_HIT = new ComponentType<>(ExampleMod.id("on_hit"), OnHitEffects::new);
    public static final ComponentType<Boolean> BREAKS_BLOCKS = new ComponentType<>(ExampleMod.id("breaks_blocks"), () -> false);

    // UX
    public static final ComponentType<RecoilProfile> CAMERA_RECOIL = new ComponentType<>(ExampleMod.id("camera_recoil"), () -> RecoilProfile.simple(10, 0));
    public static final ComponentType<Value> CHARACTER_RECOIL = new ComponentType<>(ExampleMod.id("character_recoil"), () -> Value.of(0));
    public static final ComponentType<GunShotSoundStack> GUNSHOT_SOUND = new ComponentType<>(ExampleMod.id("gunshot_sound"), () -> new GunShotSoundStack(PlayableSound.standard(SoundEvents.FIREWORK_ROCKET_BLAST), PlayableSound.of(SoundEvents.DISPENSER_FAIL, 0.75f, 1.4f, 1.6f)));
    public static final ComponentType<ParticleStack> PARTICLE_TRAIL = new ComponentType<>(ExampleMod.id("particle_trail"), ParticleStack::new);

}
