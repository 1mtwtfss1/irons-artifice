package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.Value;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.ValueStackModifier;

import java.util.Map;

public final class SeekingModifier extends ValueStackModifier {
    public SeekingModifier() {
        super(Map.of(
                ShotComponents.SEEKING, new ValueModifier(0.05, ValueModifier.Operation.ADD, ValueModifier.Type.BENEFICIAL),
//                ShotComponents.BULLET_SPEED, new ValueModifier(-0.25, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.BENEFICIAL),
                ShotComponents.GRAVITY, new ValueModifier(-0.25, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.HARMFUL)
        ));
    }

    @Override
    public void apply(ShotComponentMap components) {
        super.apply(components);
        components.set(ShotComponents.BULLET_SPEED, Value.of(3));
    }
}
