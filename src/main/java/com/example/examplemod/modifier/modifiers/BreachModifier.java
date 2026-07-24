package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.ValueStackModifier;

import java.util.Map;

public final class BreachModifier extends ValueStackModifier {
    public BreachModifier() {
        super(Map.of(
                ShotComponents.BLOCK_DAMAGE_MULTIPLIER, new ValueModifier(0.25, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.BENEFICIAL)
        ));
    }

    @Override
    public void apply(ShotComponentMap components) {
        super.apply(components);
        components.set(ShotComponents.BREAKS_BLOCKS, true);
    }
}
