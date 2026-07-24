package com.example.examplemod.gun;

import com.example.examplemod.data.ComponentType;
import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.Value;
import com.example.examplemod.item.MagazineContents;
import net.minecraft.world.item.ItemStack;

public record ShotProfile(ItemStack itemStack, GunProfile gun, MagazineContents magazineContents, ShotComponentMap components) {

    public <T> T get(ComponentType<T> type) {
        return components.getOrDefault(type);
    }

    public double value(ComponentType<Value> type) {
        return components.getOrDefault(type).compute();
    }
}
