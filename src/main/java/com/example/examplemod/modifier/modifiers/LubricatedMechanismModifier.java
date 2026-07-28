package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.ValueStackModifier;

import java.util.Map;

public final class LubricatedMechanismModifier extends ValueStackModifier {
    public LubricatedMechanismModifier() {
        super(Map.of(
                ShotComponents.RELOAD_SPEED_MULTIPLIER, new ValueModifier(.25, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.BENEFICIAL)
        ));
    }
}
