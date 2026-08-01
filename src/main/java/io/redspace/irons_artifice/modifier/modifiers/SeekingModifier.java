package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.data.Value;
import io.redspace.irons_artifice.data.ValueModifier;
import io.redspace.irons_artifice.modifier.ValueStackModifier;

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
