package io.redspace.irons_artifice.registry;

import io.redspace.irons_artifice.IronsArtifice;
import io.redspace.irons_artifice.data.PlayableSound;
import io.redspace.irons_artifice.gun.ArmPoseKind;
import io.redspace.irons_artifice.gun.Guns;
import io.redspace.irons_artifice.gun.ReloadCue;
import io.redspace.irons_artifice.gun.ReloadCueStack;
import io.redspace.irons_artifice.item.AnimationAdjuster;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.modifier.ModifierItem;
import io.redspace.irons_artifice.modifier.modifiers.AntigravityModifier;
import io.redspace.irons_artifice.modifier.modifiers.BlackpowderPayloadModifier;
import io.redspace.irons_artifice.modifier.modifiers.BreachModifier;
import io.redspace.irons_artifice.modifier.modifiers.ChainLightningModifier;
import io.redspace.irons_artifice.modifier.modifiers.FireModifier;
import io.redspace.irons_artifice.modifier.modifiers.GasVentModifier;
import io.redspace.irons_artifice.modifier.modifiers.GravityWellModifier;
import io.redspace.irons_artifice.modifier.modifiers.HairTriggerModifier;
import io.redspace.irons_artifice.modifier.modifiers.HeavyModifier;
import io.redspace.irons_artifice.modifier.modifiers.LubricatedMechanismModifier;
import io.redspace.irons_artifice.modifier.modifiers.MechanicalRepeaterModifier;
import io.redspace.irons_artifice.modifier.modifiers.OverchargedPowderModifier;
import io.redspace.irons_artifice.modifier.modifiers.ScattershotModifier;
import io.redspace.irons_artifice.modifier.modifiers.SeekingModifier;
import io.redspace.irons_artifice.modifier.modifiers.SteelCoreModifier;
import io.redspace.irons_artifice.modifier.modifiers.TrickshotModifier;
import io.redspace.irons_artifice.modifier.modifiers.WindChamberModifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(IronsArtifice.MODID);

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

    public static final DeferredItem<GunItem> FLINTLOCK_PISTOL = ITEMS.registerItem("flintlock",
            properties -> new GunItem(properties.stacksTo(1), Guns.FLINTLOCK_PISTOL, IronsArtifice.id("flintlock_pistol"), ArmPoseKind.PISTOL, MUZZLELOADER_RELOAD_CUES, null, AnimationAdjuster.LOWER_HAMMER),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> BLACKPOWDER_REVOLVER = ITEMS.registerItem("blackpowder_revolver",
            properties -> new GunItem(properties, Guns.BLACKPOWDER_REVOLVER, IronsArtifice.id("blackpowder_revolver"), ArmPoseKind.PISTOL, ReloadCueStack.of(
                    new ReloadCue(0f, PlayableSound.of(SoundRegistry.BLACKPOWDER_REVOLVER_RELOAD_START, 0.75f, 0.95f, 1.05f)),
                    new ReloadCue(1.33f / 2f, PlayableSound.of(SoundRegistry.BLACKPOWDER_REVOLVER_RELOAD_MID, 0.75f, 0.95f, 1.05f))
            ), null),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> MUSKET = ITEMS.registerItem("musket",
            properties -> new GunItem(properties.stacksTo(1), Guns.MUSKET, IronsArtifice.id("musket"), ArmPoseKind.RIFLE, ReloadCueStack.of(
                    new ReloadCue(3.1f / 3.42f, PlayableSound.of(SoundRegistry.COCK_HAMMER, 0.75f, 0.9f, 1.1f))
            ), null, AnimationAdjuster.LOWER_HAMMER),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> SIX_SHOOTER = ITEMS.registerItem("six_shooter",
            properties -> new GunItem(properties.stacksTo(1), Guns.SIX_SHOOTER, IronsArtifice.id("six_shooter"), ArmPoseKind.PISTOL, ReloadCueStack.of(
                    new ReloadCue(0.1f, PlayableSound.of(SoundRegistry.SIX_SHOOTER_HOLSTER, 0.75f, 0.95f, 1.05f)),
                    new ReloadCue(0.38f / 1.25f, PlayableSound.of(SoundRegistry.SIX_SHOOTER_EQUIP, 0.75f, 0.95f, 1.05f))
            ), PlayableSound.of(SoundRegistry.SIX_SHOOTER_EQUIP, 0.75f, 0.95f, 1.05f)),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> BLUNDERBUSS = ITEMS.registerItem("blunderbuss",
            properties -> new GunItem(properties.stacksTo(1), Guns.BLUNDERBUSS, IronsArtifice.id("blunderbuss"), ArmPoseKind.RIFLE, ReloadCueStack.of(
                    new ReloadCue(0.25f / 1.5f, PlayableSound.of(SoundRegistry.BLUNDERBUSS_RELOAD_OPEN, 0.75f, 0.95f, 1.05f)),
                    new ReloadCue(0.90f / 1.5f, PlayableSound.of(SoundRegistry.BLUNDERBUSS_RELOAD_LOAD, 0.75f, 0.95f, 1.05f)),
                    new ReloadCue(1.27f / 1.5f, PlayableSound.of(SoundRegistry.BLUNDERBUSS_RELOAD_CLOSE, 0.75f, 0.95f, 1.05f))
            ), PlayableSound.of(SoundRegistry.BLUNDERBUSS_RELOAD_CLOSE, 0.75f, 0.9f,1.1f)),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> ARQUEBUS = ITEMS.registerItem("arquebus",
            properties -> new GunItem(properties.stacksTo(1), Guns.ARQUEBUS, IronsArtifice.id("musket"), ArmPoseKind.RIFLE, ReloadCueStack.of(), null, AnimationAdjuster.LOWER_HAMMER),
            properties -> properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final DeferredItem<GunItem> CLOCKWORK_RIFLE = ITEMS.registerItem("clockwork_rifle",
            properties -> new GunItem(properties.stacksTo(1), Guns.CLOCKWORK_RIFLE, IronsArtifice.id("clockwork_rifle"), ArmPoseKind.RIFLE, ReloadCueStack.of(
                    new ReloadCue(0.38f / 1.5f, PlayableSound.of(SoundRegistry.CLOCKWORK_RIFLE_EJECT_MAG, 0.75f, 0.9f, 1.1f)),
                    new ReloadCue(1.04f / 1.5f, PlayableSound.of(SoundRegistry.CLOCKWORK_RIFLE_INSERT_MAG, 0.75f, 0.9f, 1.1f))
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
    public static final DeferredItem<ModifierItem> GRAVITY_WELL = ITEMS.registerItem(
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
    public static final DeferredItem<ModifierItem> WIND_CHAMBER = ITEMS.registerItem(
            "wind_chamber_modifier", properties -> new ModifierItem(properties.stacksTo(1), new WindChamberModifier()));
    public static final DeferredItem<ModifierItem> GAS_VENT = ITEMS.registerItem(
            "gas_vent_modifier", properties -> new ModifierItem(properties.stacksTo(1), new GasVentModifier()));
    public static final DeferredItem<ModifierItem> BLACKPOWDER_PAYLOAD = ITEMS.registerItem(
            "blackpowder_payload_modifier", properties -> new ModifierItem(properties.stacksTo(1), new BlackpowderPayloadModifier()));
    public static final DeferredItem<ModifierItem> MECHANICAL_REPEATER = ITEMS.registerItem(
            "mechanical_repeater_modifier", properties -> new ModifierItem(properties.stacksTo(1), new MechanicalRepeaterModifier()));

    public static final DeferredItem<Item> BULLET = ITEMS.registerSimpleItem("bullet");
    public static final DeferredItem<Item> BLACKPOWDER = ITEMS.registerSimpleItem("blackpowder");
}
