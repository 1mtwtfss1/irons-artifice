package com.example.examplemod.data;

import com.example.examplemod.gun.GunShotSoundStack;
import com.example.examplemod.gun.OnHitEffects;
import net.minecraft.sounds.SoundEvents;

public final class ShotComponents {
    // Shot Mechanics
    public static final ComponentType<Value> PROJECTILE_COUNT = new ComponentType<>("projectile_count", () -> Value.of(1));
    public static final ComponentType<Value> SPREAD = new ComponentType<>("spread", () -> Value.of(0));

    // Attributes
    public static final ComponentType<Value> DAMAGE = new ComponentType<>("damage", () -> Value.of(0));
    public static final ComponentType<Value> BULLET_SPEED = new ComponentType<>("bullet_speed", () -> Value.of(15));
    public static final ComponentType<Value> GRAVITY = new ComponentType<>("gravity", () -> Value.of(0.05));
    public static final ComponentType<Value> KNOCKBACK = new ComponentType<>("knockback", () -> Value.of(0));

    // Bullet Behavior
    public static final ComponentType<Value> PIERCING = new ComponentType<>("piercing", () -> Value.of(0));
    public static final ComponentType<Value> RICOCHET = new ComponentType<>("ricochet", () -> Value.of(0));
    public static final ComponentType<OnHitEffects> ON_HIT = new ComponentType<>("on_hit", OnHitEffects::new);

    // UX
    public static final ComponentType<Value> CAMERA_RECOIL = new ComponentType<>("camera_recoil", () -> Value.of(0));
    public static final ComponentType<Value> CHARACTER_RECOIL = new ComponentType<>("character_recoil", () -> Value.of(0));
    public static final ComponentType<GunShotSoundStack> GUNSHOT_SOUND = new ComponentType<>("gunshot_sound", () -> new GunShotSoundStack(PlayableSound.standard(SoundEvents.FIREWORK_ROCKET_BLAST), PlayableSound.of(SoundEvents.DISPENSER_FAIL, 0.75f, 1.4f, 1.6f)));
    public static final ComponentType<ParticleStack> PARTICLE_TRAIL = new ComponentType<>("particle_trail", ParticleStack::new);

}
