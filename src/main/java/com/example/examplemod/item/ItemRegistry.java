package com.example.examplemod.item;

import com.example.examplemod.ExampleMod;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExampleMod.MODID);

    public static final DeferredItem<GunItem> GUN = ITEMS.registerItem(
            "gun",
            GunItem::new,
            properties -> properties
                    .stacksTo(1)
                    .component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );

    private ItemRegistry() {}

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
