package com.example.examplemod.gun;

import com.example.examplemod.data.ComponentType;
import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.Value;

public record ShotProfile(ShotComponentMap components) {

    public <T> T get(ComponentType<T> type) {
        return components.getOrDefault(type);
    }

    public double value(ComponentType<Value> type) {
        return components.getOrDefault(type).compute();
    }
}
