package io.redspace.irons_artifice.modifier;

import io.redspace.irons_artifice.data.ComponentType;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.Value;
import io.redspace.irons_artifice.data.ValueModifier;
import io.redspace.irons_artifice.utils.Utils;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ValueStackModifier implements GunModifier {
    private final Map<ComponentType<Value>, ValueModifier> modifiers;

    public ValueStackModifier(Map<ComponentType<Value>, ValueModifier> modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public void apply(ShotComponentMap components) {
        modifiers.forEach((component, modifier) -> components.getOrCreate(component).addModifier(modifier));
    }

    @Override
    public List<Component> getDescriptionText() {
        return modifiers.entrySet().stream()
                .sorted(Comparator.comparingDouble(entry -> -Math.abs(entry.getValue().amount())))
                .map(entry -> Utils.formatValueModifierDescription(entry.getValue(), entry.getKey())).toList();
    }
}
