package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.GunModifier;

public final class HeavyModifier implements GunModifier {
    private static final double SPEED = -2.0;
    private static final double KNOCKBACK = 0.5;

    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.BULLET_SPEED)
                .addModifier(new ValueModifier(SPEED, ValueModifier.Operation.ADD));
        components.getOrCreate(ShotComponents.KNOCKBACK)
                .addModifier(new ValueModifier(KNOCKBACK, ValueModifier.Operation.ADD));
    }
}
