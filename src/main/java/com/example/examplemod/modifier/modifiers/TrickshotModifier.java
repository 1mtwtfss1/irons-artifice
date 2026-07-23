package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.GunModifier;

public final class TrickshotModifier implements GunModifier {
    private static final double RICOCHET = 1;

    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.RICOCHET)
                .addModifier(new ValueModifier(RICOCHET, ValueModifier.Operation.ADD));
    }
}
