package com.example.examplemod.registry;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.gun.Guns;
import com.example.examplemod.item.GunItem;
import com.example.examplemod.modifier.ModifierItem;
import com.example.examplemod.modifier.modifiers.BreachModifier;
import com.example.examplemod.modifier.modifiers.ChainLightningModifier;
import com.example.examplemod.modifier.modifiers.LubricatedMechanismModifier;
import com.example.examplemod.modifier.modifiers.OverchargedPowderModifier;
import com.example.examplemod.modifier.modifiers.FireModifier;
import com.example.examplemod.modifier.modifiers.GravityWellModifier;
import com.example.examplemod.modifier.modifiers.HairTriggerModifier;
import com.example.examplemod.modifier.modifiers.HeavyModifier;
import com.example.examplemod.modifier.modifiers.ScattershotModifier;
import com.example.examplemod.modifier.modifiers.SteelCoreModifier;
import com.example.examplemod.modifier.modifiers.TrickshotModifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExampleMod.MODID);

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static final DeferredItem<GunItem> GUN = ITEMS.registerItem("gun", properties -> new GunItem(properties.stacksTo(1), Guns.BASIC),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> GUN1 = ITEMS.registerItem("gunbang", properties -> new GunItem(properties.stacksTo(1), Guns.HAND_CANNON),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> GUN2 = ITEMS.registerItem("shotgun", properties -> new GunItem(properties.stacksTo(1), Guns.SHOTGUN),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> GUN3 = ITEMS.registerItem("gunbip", properties -> new GunItem(properties.stacksTo(1), Guns.HIGH_CAP),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );

    public static final DeferredItem<GunItem> FLINTLOCK_PISTOL = ITEMS.registerItem("flintlock", properties -> new GunItem(properties.stacksTo(1), Guns.FLINTLOCK_PISTOL),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );


    public static final DeferredItem<ModifierItem> HEAVY = ITEMS.registerItem(
            "heavy_modifier", properties -> new ModifierItem(properties.stacksTo(1), new HeavyModifier()));
    public static final DeferredItem<ModifierItem> SCATTERSHOT = ITEMS.registerItem(
            "scattershot_modifier", properties -> new ModifierItem(properties.stacksTo(1), new ScattershotModifier()));
    public static final DeferredItem<ModifierItem> TRICKSHOT = ITEMS.registerItem(
            "trickshot_modifier", properties -> new ModifierItem(properties.stacksTo(1), new TrickshotModifier()));
    public static final DeferredItem<ModifierItem> FIRE = ITEMS.registerItem(
            "fire_modifier", properties -> new ModifierItem(properties.stacksTo(1), new FireModifier()));
    public static final DeferredItem<ModifierItem> GRAVITY = ITEMS.registerItem(
            "gravity_well_modifier", properties -> new ModifierItem(properties.stacksTo(1), new GravityWellModifier()));
    public static final DeferredItem<ModifierItem> BREACH = ITEMS.registerItem(
            "breach", properties -> new ModifierItem(properties.stacksTo(1), new BreachModifier()));
    public static final DeferredItem<ModifierItem> OVERCHARGED_POWDER = ITEMS.registerItem(
            "overcharged_powder", properties -> new ModifierItem(properties.stacksTo(1), new OverchargedPowderModifier()));
    public static final DeferredItem<ModifierItem> STEEL_CORE = ITEMS.registerItem(
            "steel_core", properties -> new ModifierItem(properties.stacksTo(1), new SteelCoreModifier()));
    public static final DeferredItem<ModifierItem> HAIR_TRIGGER = ITEMS.registerItem(
            "hair_trigger", properties -> new ModifierItem(properties.stacksTo(1), new HairTriggerModifier()));
    public static final DeferredItem<ModifierItem> CHAIN_LIGHTNING = ITEMS.registerItem(
            "chain_lightning", properties -> new ModifierItem(properties.stacksTo(1), new ChainLightningModifier()));
    public static final DeferredItem<ModifierItem> LUBRICATED_MECHANISM = ITEMS.registerItem(
            "lubricated_mechanism", properties -> new ModifierItem(properties.stacksTo(1), new LubricatedMechanismModifier()));

    public static final DeferredItem<Item> BULLET = ITEMS.registerSimpleItem("bullet");


}
