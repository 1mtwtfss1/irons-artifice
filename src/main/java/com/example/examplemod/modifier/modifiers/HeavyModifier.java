package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.ValueStackModifier;

import java.util.Map;

public final class HeavyModifier extends ValueStackModifier {
    public HeavyModifier() {
        super(Map.of(
                ShotComponents.BULLET_SPEED, new ValueModifier(-0.10, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.BENEFICIAL),
                ShotComponents.KNOCKBACK, new ValueModifier(0.5, ValueModifier.Operation.ADD, ValueModifier.Type.BENEFICIAL)
        ));
    }
}
