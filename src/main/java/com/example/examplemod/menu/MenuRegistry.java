package com.example.examplemod.menu;

import com.example.examplemod.ExampleMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ExampleMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<GunMenu>> GUN_MENU = MENUS.register(
            "gun",
            () -> new MenuType<>(GunMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );

    private MenuRegistry() {}

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
