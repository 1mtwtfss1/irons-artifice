package io.redspace.irons_artifice.gun;

import io.redspace.irons_artifice.client.GunShotSoundSettings;
import io.redspace.irons_artifice.data.PlayableSound;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.data.Value;
import io.redspace.irons_artifice.recoil.RecoilProfile;
import io.redspace.irons_artifice.registry.SoundRegistry;
import net.minecraft.sounds.SoundEvents;

// todo: should probably be converted into an item component, rather than hardcoded to gunitem
public final class Guns {

    public static final GunProfile BASIC = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.GUNSHOT_SOUND, new GunShotSoundStack(
                        new GunShotSoundSettings(SoundRegistry.REVOLVER_SHOOT, 0.9f, 1.1f, -1f, 0f, 128f),
                        new GunShotSoundSettings(SoundRegistry.BULLET_ECHO_GENERIC, 0.6f, 0.7f, 48f, 112f, 192f),
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
                        new GunShotSoundSettings(SoundRegistry.FLINTLOCK_SHOOT, 0.9f, 1.1f, -1f, 0f, 128f),
                        new GunShotSoundSettings(SoundRegistry.BULLET_ECHO_MUZZLELOADER, 0.9f, 1.1f, 48f, 96f, 192f),
                        PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f)
                ));
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(35f, .45f, 1.7f, 0));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.25));
                map.set(ShotComponents.SPREAD, Value.of(3));
                map.set(ShotComponents.FIRE_DELAY, Value.of(1));
                map.set(ShotComponents.MUZZLE_FLASH, MuzzleFlashSettings.of(1.5f, MuzzleFlashType.LARGE));
                return map;
            },
            1,
            3,
            60,
            FireMode.SEMI
    );

    public static final GunProfile BLACKPOWDER_REVOLVER = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(25f, .33f, 1.7f, 0));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.125));
                map.set(ShotComponents.FIRE_DELAY, Value.of(10));
                map.set(ShotComponents.DAMAGE, Value.of(10));
                map.set(ShotComponents.GUNSHOT_SOUND, new GunShotSoundStack(
                        new GunShotSoundSettings(SoundRegistry.BLACKPOWDER_REVOLVER_SHOOT, 0.9f, 1.1f, -1f, 0f, 128f),
                        new GunShotSoundSettings(SoundRegistry.BULLET_ECHO_GENERIC, 0.5f, 0.7f, 48f, 96f, 192f),
                        PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f)
                ));
                map.set(ShotComponents.MUZZLE_FLASH, MuzzleFlashSettings.of(1.5f, MuzzleFlashType.LARGE));
                return map;
            },
            6,
            5,
            40,
            FireMode.SEMI
    );


    public static final GunProfile SIX_SHOOTER = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(15f, .5f, 2.7f, 465));
                map.set(ShotComponents.FIRE_DELAY, Value.of(3));
                map.set(ShotComponents.DAMAGE, Value.of(6));
                map.set(ShotComponents.GUNSHOT_SOUND, new GunShotSoundStack(
                        new GunShotSoundSettings(SoundRegistry.SIX_SHOOTER_SHOOT, 0.9f, 1.1f, -1f, 0f, 128f),
                        new GunShotSoundSettings(SoundRegistry.BULLET_ECHO_GENERIC, 0.9f, 1.1f, 48f, 96f, 192f),
                        PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f)
                ));
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
                map.set(ShotComponents.GUNSHOT_SOUND, new GunShotSoundStack(
                        new GunShotSoundSettings(SoundRegistry.MUSKET_SHOOT, 0.9f, 1.1f, -1f, 0f, 128f),
                        new GunShotSoundSettings(SoundRegistry.BULLET_ECHO_MUZZLELOADER, 1f, 1.3f, 48f, 96f, 192f),
                        PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f)
                ));
                map.set(ShotComponents.MUZZLE_FLASH, MuzzleFlashSettings.of(2.5f, MuzzleFlashType.LARGE));
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
                map.set(ShotComponents.SPREAD, Value.of(9));
                map.set(ShotComponents.FIRE_DELAY, Value.of(1));
                map.set(ShotComponents.GUNSHOT_SOUND, new GunShotSoundStack(
                        new GunShotSoundSettings(SoundRegistry.BLUNDERBUSS_SHOOT, 0.9f, 1.1f, -1f, 0f, 128f),
                        new GunShotSoundSettings(SoundRegistry.BULLET_ECHO_MUZZLELOADER, 0.7f, 0.8f, 48f, 96f, 192f),
                        PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f)
                ));
                map.set(ShotComponents.MUZZLE_FLASH, MuzzleFlashSettings.of(2f, MuzzleFlashType.LARGE));
                return map;
            },
            2,
            5,
            30,
            FireMode.SEMI
    );


    public static final GunProfile ARQUEBUS = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(30f, .45f, -1.9f, 222));
                map.set(ShotComponents.SPREAD, Value.of(1));
                map.set(ShotComponents.FIRE_DELAY, Value.of(1));
                map.set(ShotComponents.DAMAGE, Value.of(14));
                map.set(ShotComponents.MUZZLE_FLASH, MuzzleFlashSettings.of(2f, MuzzleFlashType.LARGE));
                map.set(ShotComponents.GUNSHOT_SOUND, new GunShotSoundStack(
                        new GunShotSoundSettings(SoundRegistry.ARQUEBUS_SHOOT, 0.9f, 1.1f, -1f, 0f, 128f),
                        new GunShotSoundSettings(SoundRegistry.BULLET_ECHO_GENERIC, 1.2f, 1.5f, 48f, 96f, 192f),
                        PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f)
                ));
                return map;
            },
            1,
            7,
            30,
            FireMode.SEMI
    );

    public static final GunProfile CLOCKWORK_RIFLE = new GunProfile(
            () -> {
                var map = basicGun();
                map.set(ShotComponents.CAMERA_RECOIL, RecoilProfile.of(7.5f, .35f, 0.6f, 6969));
                map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.05));
                map.set(ShotComponents.FIRE_DELAY, Value.of(4));
                map.set(ShotComponents.SPREAD, Value.of(2));
                map.set(ShotComponents.DAMAGE, Value.of(5));
                map.set(ShotComponents.MUZZLE_FLASH, MuzzleFlashSettings.of(2f, MuzzleFlashType.TRIANGLE, MuzzleFlashType.SMALL_STAR));
                map.set(ShotComponents.GUNSHOT_SOUND, new GunShotSoundStack(
                        new GunShotSoundSettings(SoundRegistry.CLOCKWORK_RIFLE_SHOOT, 0.9f, 1.1f, -1f, 0f, 128f),
                        new GunShotSoundSettings(SoundRegistry.BULLET_ECHO_GENERIC, 0.9f, 1.1f, 48f, 96f, 192f),
                        PlayableSound.of(PlayableSound.holder(SoundEvents.DISPENSER_FAIL), 0.75f, 1.4f, 1.6f)
                ));
                return map;
            },
            10,
            6,
            30,
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
