package com.example.examplemod.modifier;

import com.example.examplemod.data.ShotComponentMap;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface GunModifier {
    void apply(ShotComponentMap components);

    default List<Component> getDescriptionText() {
        return List.of();
    }
}
