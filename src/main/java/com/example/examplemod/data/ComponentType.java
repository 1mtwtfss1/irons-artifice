package com.example.examplemod.data;

import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public final class ComponentType<T> {
    final Identifier name;

    final Supplier<T> defaultValue;

    public ComponentType(Identifier name, Supplier<T> defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public T provideDefaultValue() {
        return defaultValue.get();
    }

    public Identifier getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("ComponentType[%s]", name);
    }
}
