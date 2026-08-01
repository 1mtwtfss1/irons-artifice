package com.example.examplemod.registry;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.data.PlayableSound;
import com.example.examplemod.gun.ArmPoseKind;
import com.example.examplemod.gun.Guns;
import com.example.examplemod.gun.ReloadCue;
import com.example.examplemod.gun.ReloadCueStack;
import com.example.examplemod.item.AnimationAdjuster;
import com.example.examplemod.item.GunItem;
import com.example.examplemod.modifier.ModifierItem;
import com.example.examplemod.modifier.modifiers.AntigravityModifier;
import com.example.examplemod.modifier.modifiers.BreachModifier;
import com.example.examplemod.modifier.modifiers.ChainLightningModifier;
import com.example.examplemod.modifier.modifiers.FireModifier;
import com.example.examplemod.modifier.modifiers.GravityWellModifier;
import com.example.examplemod.modifier.modifiers.HairTriggerModifier;
import com.example.examplemod.modifier.modifiers.HeavyModifier;
import com.example.examplemod.modifier.modifiers.LubricatedMechanismModifier;
import com.example.examplemod.modifier.modifiers.OverchargedPowderModifier;
import com.example.examplemod.modifier.modifiers.ScattershotModifier;
import com.example.examplemod.modifier.modifiers.SeekingModifier;
import com.example.examplemod.modifier.modifiers.SteelCoreModifier;
import com.example.examplemod.modifier.modifiers.TrickshotModifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
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

    private static final ReloadCueStack REVOLVER_RELOAD_CUES = ReloadCueStack.of(
            new ReloadCue(0.20f, PlayableSound.of(SoundEvents.CROSSBOW_LOADING_MIDDLE, 0.7f, 1.0f)),
            new ReloadCue(0.55f, PlayableSound.of(SoundRegistry.COCK_HAMMER, 0.8f, 0.95f, 1.05f)),
            new ReloadCue(0.85f, PlayableSound.of(PlayableSound.holder(SoundEvents.IRON_TRAPDOOR_CLOSE), 0.6f, 1.2f))
    );

    private static final ReloadCueStack MUZZLELOADER_RELOAD_CUES = ReloadCueStack.of(
            new ReloadCue(0.25f, PlayableSound.of(SoundEvents.CROSSBOW_LOADING_MIDDLE, 0.75f, 0.9f)),
            new ReloadCue(0.60f, PlayableSound.of(SoundRegistry.COCK_HAMMER, 0.85f, 0.85f, 0.95f)),
            new ReloadCue(0.90f, PlayableSound.of(SoundEvents.CROSSBOW_LOADING_END, 0.8f, 1.0f))
    );

    public static final DeferredItem<GunItem> GUN = ITEMS.registerItem("gun",
            properties -> new GunItem(properties.stacksTo(1), Guns.BASIC, ExampleMod.id("example_revolver"), ArmPoseKind.PISTOL, REVOLVER_RELOAD_CUES, null),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> GUN1 = ITEMS.registerItem("gunbang",
            properties -> new GunItem(properties.stacksTo(1), Guns.HAND_CANNON, ArmPoseKind.PISTOL),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> GUN2 = ITEMS.registerItem("shotgun",
            properties -> new GunItem(properties.stacksTo(1), Guns.SHOTGUN, ArmPoseKind.PISTOL),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> GUN3 = ITEMS.registerItem("gunbip",
            properties -> new GunItem(properties.stacksTo(1), Guns.HIGH_CAP, ArmPoseKind.RIFLE),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );

    public static final DeferredItem<GunItem> FLINTLOCK_PISTOL = ITEMS.registerItem("flintlock",
            properties -> new GunItem(properties.stacksTo(1), Guns.FLINTLOCK_PISTOL, ExampleMod.id("flintlock_pistol"), ArmPoseKind.PISTOL, MUZZLELOADER_RELOAD_CUES, null, AnimationAdjuster.LOWER_HAMMER),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> BLACKPOWDER_REVOLVER = ITEMS.registerItem("blackpowder_revolver",
            properties -> new GunItem(properties, Guns.BLACKPOWDER_REVOLVER, ExampleMod.id("hand_cannon"), ArmPoseKind.PISTOL, ReloadCueStack.of(
                    new ReloadCue(0f, PlayableSound.of(SoundRegistry.BLACKPOWDER_REVOLVER_RELOAD_START, 0.75f, 0.95f, 1.05f)),
                    new ReloadCue(1.75f / 2.58f, PlayableSound.of(SoundRegistry.BLACKPOWDER_REVOLVER_RELOAD_MID, 0.75f, 0.95f, 1.05f))
            ), null),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> MUSKET = ITEMS.registerItem("musket",
            properties -> new GunItem(properties.stacksTo(1), Guns.MUSKET, ExampleMod.id("musket"), ArmPoseKind.RIFLE, ReloadCueStack.of(
                    new ReloadCue(3.1f / 3.42f, PlayableSound.of(SoundRegistry.COCK_HAMMER, 0.75f, 0.9f, 1.1f))
            ), null, AnimationAdjuster.LOWER_HAMMER),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> SIX_SHOOTER = ITEMS.registerItem("six_shooter",
            properties -> new GunItem(properties.stacksTo(1), Guns.SIX_SHOOTER, ExampleMod.id("six_shooter"), ArmPoseKind.PISTOL, ReloadCueStack.of(
                    new ReloadCue(0.1f, PlayableSound.of(SoundRegistry.SIX_SHOOTER_HOLSTER, 0.75f, 0.95f, 1.05f)),
                    new ReloadCue(0.38f / 1.25f, PlayableSound.of(SoundRegistry.SIX_SHOOTER_EQUIP, 0.75f, 0.95f, 1.05f))
            ), PlayableSound.of(SoundRegistry.SIX_SHOOTER_EQUIP, 0.75f, 0.95f, 1.05f)),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> BLUNDERBUSS = ITEMS.registerItem("blunderbuss",
            properties -> new GunItem(properties.stacksTo(1), Guns.BLUNDERBUSS, ExampleMod.id("blunderbuss"), ArmPoseKind.RIFLE, ReloadCueStack.of(
                    new ReloadCue(0.25f / 1.92f, PlayableSound.of(SoundRegistry.BLUNDERBUSS_RELOAD_OPEN, 0.75f, 0.95f, 1.05f)),
                    new ReloadCue(0.90f / 1.92f, PlayableSound.of(SoundRegistry.BLUNDERBUSS_RELOAD_LOAD, 0.75f, 0.95f, 1.05f)),
                    new ReloadCue(1.27f / 1.92f, PlayableSound.of(SoundRegistry.BLUNDERBUSS_RELOAD_CLOSE, 0.75f, 0.95f, 1.05f))
                    ), null),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> ARQUEBUS = ITEMS.registerItem("arquebus",
            properties -> new GunItem(properties.stacksTo(1), Guns.ARQUEBUS, ExampleMod.id("musket"), ArmPoseKind.RIFLE, ReloadCueStack.of(), null, AnimationAdjuster.LOWER_HAMMER),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> CLOCKWORK_RIFLE = ITEMS.registerItem("clockwork_rifle",
            properties -> new GunItem(properties.stacksTo(1), Guns.CLOCKWORK_RIFLE, ExampleMod.id("clockwork_rifle"), ArmPoseKind.RIFLE, ReloadCueStack.of(
                    new ReloadCue(0.38f / 1.63f, PlayableSound.of(SoundRegistry.CLOCKWORK_RIFLE_EJECT_MAG, 0.75f, 0.9f, 1.1f)),
                    new ReloadCue(1.04f / 1.63f, PlayableSound.of(SoundRegistry.CLOCKWORK_RIFLE_INSERT_MAG, 0.75f, 0.9f, 1.1f))
            ), null, AnimationAdjuster.HARMONICA_MAGAZINE),
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
            "breach_modifier", properties -> new ModifierItem(properties.stacksTo(1), new BreachModifier()));
    public static final DeferredItem<ModifierItem> OVERCHARGED_POWDER = ITEMS.registerItem(
            "overcharged_powder_modifier", properties -> new ModifierItem(properties.stacksTo(1), new OverchargedPowderModifier()));
    public static final DeferredItem<ModifierItem> STEEL_CORE = ITEMS.registerItem(
            "steel_core_modifier", properties -> new ModifierItem(properties.stacksTo(1), new SteelCoreModifier()));
    public static final DeferredItem<ModifierItem> HAIR_TRIGGER = ITEMS.registerItem(
            "hair_trigger_modifier", properties -> new ModifierItem(properties.stacksTo(1), new HairTriggerModifier()));
    public static final DeferredItem<ModifierItem> CHAIN_LIGHTNING = ITEMS.registerItem(
            "chain_lightning_modifier", properties -> new ModifierItem(properties.stacksTo(1), new ChainLightningModifier()));
    public static final DeferredItem<ModifierItem> LUBRICATED_MECHANISM = ITEMS.registerItem(
            "lubricated_mechanism_modifier", properties -> new ModifierItem(properties.stacksTo(1), new LubricatedMechanismModifier()));
    public static final DeferredItem<ModifierItem> ANTIGRAVITY_MODIFIER = ITEMS.registerItem(
            "antigravity_modifier", properties -> new ModifierItem(properties.stacksTo(1), new AntigravityModifier()));
    public static final DeferredItem<ModifierItem> SEEKING = ITEMS.registerItem(
            "seeking_modifier", properties -> new ModifierItem(properties.stacksTo(1), new SeekingModifier()));

    public static final DeferredItem<Item> BULLET = ITEMS.registerSimpleItem("bullet");
}
