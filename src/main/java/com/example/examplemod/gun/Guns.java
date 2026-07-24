package com.example.examplemod.gun;

import com.example.examplemod.data.PlayableSound;
import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.Value;
import net.minecraft.sounds.SoundEvents;

public final class Guns {

    public static final GunProfile BASIC = new GunProfile(
            Guns::basicGun,
            6,
            5,
            20,
            FireMode.SEMI
    );

    public static final GunProfile HAND_CANNON = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.CAMERA_RECOIL, Value.of(25));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.25));
                map.set(ShotComponents.FIRE_DELAY, Value.of(1));
                map.set(ShotComponents.GUNSHOT_SOUND,
                        new GunShotSoundStack(PlayableSound.of(SoundEvents.FIREWORK_ROCKET_BLAST, 8f, 0.75f, 0.85f), PlayableSound.of(SoundEvents.DISPENSER_FAIL, 0.75f, 1.4f, 1.6f))
                );
                return map;
            },
            6,
            5,
            20,
            FireMode.SEMI
    );

    public static final GunProfile SHOTGUN = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.DAMAGE, Value.of(10));
                map.set(ShotComponents.PROJECTILE_COUNT, Value.of(6));
                map.set(ShotComponents.SPREAD, Value.of(5));
                map.set(ShotComponents.CAMERA_RECOIL, Value.of(30));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(1.5));
                map.set(ShotComponents.GUNSHOT_SOUND,
                        new GunShotSoundStack(PlayableSound.standard(SoundEvents.FIREWORK_ROCKET_LARGE_BLAST), PlayableSound.of(SoundEvents.DISPENSER_FAIL, 0.75f, 1.4f, 1.6f))
                );
                return map;
            },
            6,
            5,
            20,
            FireMode.SEMI
    );
    public static final GunProfile HIGH_CAP = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.CAMERA_RECOIL, Value.of(10));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.05));
                map.set(ShotComponents.FIRE_DELAY, Value.of(2));
                return map;
            },
            30,
            5,
            20,
            FireMode.AUTO
    );

    private static ShotComponentMap basicGun() {
        ShotComponentMap map = new ShotComponentMap();
        map.set(ShotComponents.PROJECTILE_COUNT, Value.of(1));
        map.set(ShotComponents.SPREAD, Value.of(1.0));
        map.set(ShotComponents.DAMAGE, Value.of(6.0));
        map.set(ShotComponents.BULLET_SPEED, Value.of(15.0));
        map.set(ShotComponents.GRAVITY, Value.of(0.05));
        map.set(ShotComponents.KNOCKBACK, Value.of(0.3));
        map.set(ShotComponents.CAMERA_RECOIL, Value.of(10.0));
        map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.0));
        map.set(ShotComponents.FIRE_DELAY, Value.of(5));
        return map;
    }
}
