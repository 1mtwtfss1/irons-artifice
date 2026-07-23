package com.example.examplemod.data;

import java.util.function.Supplier;

public final class ComponentType<T> {
    final String name;

    final Supplier<T> defaultValue;

    public ComponentType(String name, Supplier<T> defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public T provideDefaultValue() {
        return defaultValue.get();
    }

    @Override
    public String toString() {
        return String.format("ComponentType[%s]", name);
    }
}
