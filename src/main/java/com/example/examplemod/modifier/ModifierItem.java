package com.example.examplemod.modifier;

import net.minecraft.world.item.Item;

public class ModifierItem extends Item {
    private final GunModifier modifier;

    public ModifierItem(Properties properties, GunModifier modifier) {
        super(properties);
        this.modifier = modifier;
    }

    public GunModifier getModifier() {
        return modifier;
    }
}
