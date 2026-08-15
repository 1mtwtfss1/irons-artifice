package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.data.ValueModifier;
import io.redspace.irons_artifice.modifier.ValueStackModifier;

import java.util.Map;

public final class EnchantedBulletModifier extends ValueStackModifier {
    public EnchantedBulletModifier() {
        super(Map.of(
                ShotComponents.AMMO_CONSUME_CHANCE, new ValueModifier(-0.25, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.HARMFUL)
        ));
    }
}
