package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.ValueStackModifier;

import java.util.Map;

// fixme: fix this awful name
public final class HairTriggerModifier extends ValueStackModifier {
    public HairTriggerModifier() {
        super(Map.of(
                ShotComponents.FIRE_DELAY, new ValueModifier(-0.25, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.HARMFUL)
        ));
    }
}
