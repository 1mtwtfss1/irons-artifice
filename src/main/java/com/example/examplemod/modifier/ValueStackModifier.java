package com.example.examplemod.modifier;

import com.example.examplemod.data.ComponentType;
import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.Value;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.utils.Utils;
import net.minecraft.network.chat.Component;

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
        return modifiers.entrySet().stream().map(entry -> Utils.formatValueModifierDescription(entry.getValue(), entry.getKey())).toList();
    }
}
