package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.ValueStackModifier;

import java.util.Map;

public final class ScattershotModifier extends ValueStackModifier {
    public ScattershotModifier() {
        super(Map.of(
                ShotComponents.PROJECTILE_COUNT, new ValueModifier(3, ValueModifier.Operation.ADD, ValueModifier.Type.BENEFICIAL),
                ShotComponents.SPREAD, new ValueModifier(3, ValueModifier.Operation.ADD, ValueModifier.Type.HARMFUL)
        ));
    }
}
