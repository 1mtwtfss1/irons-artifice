package com.example.examplemod.gun;

import com.example.examplemod.client.GunShotSoundSettings;
import com.example.examplemod.data.PlayableSound;
import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.Value;
import com.example.examplemod.recoil.RecoilProfile;
import com.example.examplemod.registry.SoundRegistry;
import net.minecraft.sounds.SoundEvents;

// todo: should probably be converted into an item component, rather than hardcoded to gunitem
public final class Guns {

    public static final GunProfile BASIC = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.GUNSHOT_SOUND, new GunShotSoundStack(
                        new GunShotSoundSettings(SoundRegistry.REVOLVER_SHOOT, 0.9f, 1.1f, -1f, 0f, 128f),
                        new GunShotSoundSettings(SoundRegistry.GENERIC_BULLET_ECHO, 0.6f, 0.7f, 48f, 112f, 192f),
                        PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f)
                ));
                return map;
            },
            6,
            5,
            40,
            FireMode.SEMI
    );

    public static final GunProfile FLINTLOCK_PISTOL = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.DAMAGE, Value.of(14));
                map.set(ShotComponents.GUNSHOT_SOUND, new GunShotSoundStack(
                        new GunShotSoundSettings(SoundRegistry.REVOLVER_SHOOT, 0.7f, 0.9f, -1f, 0f, 128f),
                        new GunShotSoundSettings(SoundRegistry.GENERIC_BULLET_ECHO, 0.5f, 0.6f, 48f, 112f, 192f),
                        PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f)
                ));
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(35f, .45f, 1.7f, 0));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.25));
                map.set(ShotComponents.SPREAD, Value.of(3));
                map.set(ShotComponents.FIRE_DELAY, Value.of(1));
                return map;
            },
            1,
            3,
            80,
            FireMode.SEMI
    );

    public static final GunProfile BLACKPOWDER_REVOLVER = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(25f, .33f, 1.7f, 0));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.125));
                map.set(ShotComponents.FIRE_DELAY, Value.of(10));
                map.set(ShotComponents.DAMAGE, Value.of(10));
                map.set(ShotComponents.GUNSHOT_SOUND,
                        new GunShotSoundStack(GunShotSoundSettings.of(SoundEvents.FIREWORK_ROCKET_BLAST, 0.75f, 0.85f, -1f, 0f, 48f), PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f))
                );
                return map;
            },
            6,
            5,
            50,
            FireMode.SEMI
    );


    public static final GunProfile SIX_SHOOTER = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(15f, .5f, 2.7f, 465));
                map.set(ShotComponents.FIRE_DELAY, Value.of(3));
                map.set(ShotComponents.DAMAGE, Value.of(6));
                return map;
            },
            6,
            5,
            20,
            FireMode.SEMI
    );

    public static final GunProfile MUSKET = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.DAMAGE, Value.of(18));
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(30f, .45f, 1.9f, 111));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.25));
                map.set(ShotComponents.SPREAD, Value.of(0.5));
                map.set(ShotComponents.FIRE_DELAY, Value.of(1));
                map.set(ShotComponents.GUNSHOT_SOUND,
                        new GunShotSoundStack(GunShotSoundSettings.of(SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, 0.9f, 1.1f, -1f, 0f, 48f), PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f))
                );
                return map;
            },
            1,
            5,
            60,
            FireMode.SEMI
    );

    public static final GunProfile BLUNDERBUSS = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.DAMAGE, Value.of(21));
                map.set(ShotComponents.PROJECTILE_COUNT, Value.of(6));
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(30f, .35f, 2f, 999));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.75));
                map.set(ShotComponents.SPREAD, Value.of(8));
                map.set(ShotComponents.FIRE_DELAY, Value.of(1));
                map.set(ShotComponents.GUNSHOT_SOUND,
                        new GunShotSoundStack(GunShotSoundSettings.of(SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, 0.9f, 1.1f, -1f, 0f, 48f), PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f))
                );
                return map;
            },
            2,
            5,
            32,
            FireMode.SEMI
    );


    public static final GunProfile ARQUEBUS = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(30f, .45f, -1.9f, 222));
                map.set(ShotComponents.SPREAD, Value.of(1));
                map.set(ShotComponents.FIRE_DELAY, Value.of(1));
                map.set(ShotComponents.DAMAGE, Value.of(14));
                return map;
            },
            1,
            7,
            20,
            FireMode.SEMI
    );

    public static final GunProfile CLOCKWORK_RIFLE = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(7.5f, .35f, 0.6f, 6969));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.05));
                map.set(ShotComponents.FIRE_DELAY, Value.of(4));
                map.set(ShotComponents.SPREAD, Value.of(2));
                map.set(ShotComponents.DAMAGE, Value.of(4));
                return map;
            },
            10,
            6,
            20,
            FireMode.AUTO
    );

    public static final GunProfile HAND_CANNON = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(25f, .33f, 1.7f, 0));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.125));
                map.set(ShotComponents.FIRE_DELAY, Value.of(1));
                map.set(ShotComponents.DAMAGE, Value.of(10));
                map.set(ShotComponents.GUNSHOT_SOUND,
                        new GunShotSoundStack(GunShotSoundSettings.of(SoundEvents.FIREWORK_ROCKET_BLAST, 0.75f, 0.85f, -1f, 0f, 48f), PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f))
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
                map.set(ShotComponents.DAMAGE, Value.of(18));
                map.set(ShotComponents.PROJECTILE_COUNT, Value.of(6));
                map.set(ShotComponents.SPREAD, Value.of(5));
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.simple(30f, 999));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(1.5));
                map.set(ShotComponents.FIRE_DELAY, Value.of(1));
                map.set(ShotComponents.GUNSHOT_SOUND,
                        new GunShotSoundStack(GunShotSoundSettings.of(SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, 0.9f, 1.1f, -1f, 0f, 48f), PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f))
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
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(7.5f, .45f, 0.3f, 69));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.05));
                map.set(ShotComponents.FIRE_DELAY, Value.of(2));
                map.set(ShotComponents.DAMAGE, Value.of(3));
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
        map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(10f, .33f, 1.7f, 431));
        map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.0));
        map.set(ShotComponents.FIRE_DELAY, Value.of(20));
        return map;
    }
}
