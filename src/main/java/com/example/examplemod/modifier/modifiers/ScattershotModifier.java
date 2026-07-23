package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.GunModifier;

public final class ScattershotModifier implements GunModifier {
    private static final double PROJECTILES = 2;
    private static final double SPREAD = 3.0;

    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.PROJECTILE_COUNT)
                .addModifier(new ValueModifier(PROJECTILES, ValueModifier.Operation.ADD));
        components.getOrCreate(ShotComponents.SPREAD)
                .addModifier(new ValueModifier(SPREAD, ValueModifier.Operation.ADD));
    }
}
