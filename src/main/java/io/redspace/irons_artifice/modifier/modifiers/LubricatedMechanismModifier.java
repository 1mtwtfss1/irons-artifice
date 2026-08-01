package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.data.ValueModifier;
import io.redspace.irons_artifice.modifier.ValueStackModifier;

import java.util.Map;

public final class LubricatedMechanismModifier extends ValueStackModifier {
    public LubricatedMechanismModifier() {
        super(Map.of(
                ShotComponents.RELOAD_SPEED_MULTIPLIER, new ValueModifier(.25, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.BENEFICIAL)
        ));
    }
}
