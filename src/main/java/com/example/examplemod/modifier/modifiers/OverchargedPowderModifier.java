package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.ValueStackModifier;

import java.util.Map;

public final class OverchargedPowderModifier extends ValueStackModifier {
    public OverchargedPowderModifier() {
        super(Map.of(
                ShotComponents.BULLET_SPEED, new ValueModifier(0.25, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.BENEFICIAL),
                ShotComponents.CAMERA_RECOIL_MULTIPLIER, new ValueModifier(0.20, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.HARMFUL),
                ShotComponents.DAMAGE, new ValueModifier(0.15, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.BENEFICIAL)
        ));
    }
}
