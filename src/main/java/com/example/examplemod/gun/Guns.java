package com.example.examplemod.gun;

import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.Value;

public final class Guns {

    public static final GunProfile REVOLVER = new GunProfile(
            Guns::revolverBase,
            6,
            5,
            1
    );

    private static ShotComponentMap revolverBase() {
        ShotComponentMap map = new ShotComponentMap();
        map.set(ShotComponents.PROJECTILE_COUNT, Value.of(1));
        map.set(ShotComponents.SPREAD, Value.of(1.0));
        map.set(ShotComponents.DAMAGE, Value.of(6.0));
        map.set(ShotComponents.BULLET_SPEED, Value.of(15.0));
        map.set(ShotComponents.GRAVITY, Value.of(0.05));
        map.set(ShotComponents.KNOCKBACK, Value.of(0.3));
        map.set(ShotComponents.CAMERA_RECOIL, Value.of(10.0));
        map.set(ShotComponents.CHARACTER_RECOIL, Value.of(0.0));
        return map;
    }
}
