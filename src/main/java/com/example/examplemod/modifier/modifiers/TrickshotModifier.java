package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.ValueStackModifier;

import java.util.Map;

public final class TrickshotModifier extends ValueStackModifier {
    public TrickshotModifier() {
        super(Map.of(
                ShotComponents.RICOCHET, new ValueModifier(1, ValueModifier.Operation.ADD, ValueModifier.Type.BENEFICIAL)
        ));
    }
}
