package com.example.examplemod.utils;

import com.example.examplemod.data.ComponentType;
import com.example.examplemod.data.ValueModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public class Utils {

    public static Vec3 reflect(Vec3 direction, Vec3 normal) {
        return direction.subtract(normal.scale(2 * normal.dot(direction)));
    }

    public static Component formatValueModifierDescription(ValueModifier valueModifier, ComponentType<?> componentType) {
        return formatValueModifierDescription(valueModifier, Component.translatable(String.format("%s.component_type.%s", componentType.getName().getNamespace(), componentType.getName().getPath())));
    }

    public static Component formatValueModifierDescription(ValueModifier valueModifier, Component valueName) {
        String identifier = valueModifier.amount() < 0 ? "minus" : "plus";
        double value = valueModifier.amount();
        if (valueModifier.operation() != ValueModifier.Operation.ADD) {
            identifier += "_percent";
            value *= 100;
        }
        return Component.translatable(String.format("examplemod.value_modifier.%s", identifier), value, valueName);
    }
}
