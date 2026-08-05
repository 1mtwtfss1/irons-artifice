package io.redspace.irons_artifice.datagen;

import com.geckolib.renderer.internal.GeckolibItemSpecialRenderer;
import io.redspace.irons_artifice.IronsArtifice;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.modifier.ModifierItem;
import io.redspace.irons_artifice.registry.ItemRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ItemModelDataGenerator extends ModelProvider {
    private static final Identifier GECKOLIB_GUN_DISPLAY = IronsArtifice.id("item/example_gun_display");
    private static final Identifier DEMO_GUN_MODEL = IronsArtifice.id("item/gun");

    public ItemModelDataGenerator(PackOutput output) {
        super(output, IronsArtifice.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        for (DeferredItem<Item> item : List.of(ItemRegistry.BULLET, ItemRegistry.BLACKPOWDER, ItemRegistry.SIMPLE_MECHANICAL_COMPONENTS, ItemRegistry.MECHANICAL_COMPONENTS, ItemRegistry.CLOCKWORK_COMPONENTS)) {
            generateTemplatedItem(itemModels, item.get(), itemTexture(item));
        }

        for (var entry : modifiers().entrySet()) {
            generateTemplatedItem(itemModels, entry.getKey().get(), itemTexture(entry.getValue()));
        }

        for (DeferredItem<GunItem> gun : geckolibGuns()) {
            itemModels.itemModelOutput.accept(
                    gun.get(),
                    ItemModelUtils.specialModel(GECKOLIB_GUN_DISPLAY, new GeckolibItemSpecialRenderer.Unbaked<>())
            );
        }
    }

    /**
     * Writes {@code models/item/<item>.json} from {@link ModelTemplates#FLAT_ITEM}
     * with the given layer0 texture, plus the matching {@code items/<item>.json} client item.
     */
    private static void generateTemplatedItem(ItemModelGenerators itemModels, Item item, Identifier layer0Texture) {
        Identifier modelLocation = ModelLocationUtils.getModelLocation(item);
        ModelTemplates.FLAT_ITEM.create(
                modelLocation,
                TextureMapping.layer0(new Material(layer0Texture)),
                itemModels.modelOutput
        );
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLocation));
    }

    private static Identifier itemTexture(DeferredItem<?> item) {
        return itemTexture(item.getId());
    }

    private static Identifier itemTexture(Identifier identifier) {
        return identifier.withPrefix("item/");
    }

    private static Map<DeferredItem<ModifierItem>, Identifier> modifiers() {
        Map<DeferredItem<ModifierItem>, Identifier> map = new HashMap<>();
        map.put(ItemRegistry.HEAVY, IronsArtifice.id("lead_core"));
        map.put(ItemRegistry.SCATTERSHOT, Identifier.withDefaultNamespace("gunpowder"));
        map.put(ItemRegistry.TRICKSHOT, Identifier.withDefaultNamespace("ender_pearl"));
        map.put(ItemRegistry.FIRE, IronsArtifice.id("incendiary_tip"));
        map.put(ItemRegistry.GRAVITY_WELL, Identifier.withDefaultNamespace("nether_star"));
        map.put(ItemRegistry.BREACH, Identifier.withDefaultNamespace("flint"));
        map.put(ItemRegistry.OVERCHARGED_POWDER, Identifier.withDefaultNamespace("redstone"));
        map.put(ItemRegistry.STEEL_CORE, IronsArtifice.id("steel_core"));
        map.put(ItemRegistry.HAIR_TRIGGER, Identifier.withDefaultNamespace("breeze_rod"));
        map.put(ItemRegistry.CHAIN_LIGHTNING, Identifier.withDefaultNamespace("copper_ingot"));
        map.put(ItemRegistry.CHAIN_SHOT, Identifier.withDefaultNamespace("iron_chain"));
        map.put(ItemRegistry.FROZEN_JACKET, IronsArtifice.id("frozen_jacket"));
        map.put(ItemRegistry.FAIRY_DUST, Identifier.withDefaultNamespace("glow_berries"));
        map.put(ItemRegistry.VENOM_CAPSULE, Identifier.withDefaultNamespace("spider_eye"));
        map.put(ItemRegistry.LUBRICATED_MECHANISM, Identifier.withDefaultNamespace("honey_bottle"));
        map.put(ItemRegistry.ANTIGRAVITY_MODIFIER, Identifier.withDefaultNamespace("feather"));
        map.put(ItemRegistry.SEEKING, Identifier.withDefaultNamespace("ender_eye"));
        map.put(ItemRegistry.WIND_CHAMBER, Identifier.withDefaultNamespace("wind_charge"));
        map.put(ItemRegistry.GAS_VENT, Identifier.withDefaultNamespace("hopper"));
        map.put(ItemRegistry.BLACKPOWDER_CHARGE, Identifier.withDefaultNamespace("fire_charge"));
        map.put(ItemRegistry.MECHANICAL_REPEATER, Identifier.withDefaultNamespace("repeater"));
        return map;
    }

    private static List<DeferredItem<GunItem>> geckolibGuns() {
        return ItemRegistry.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof GunItem)
                .map(h -> (DeferredItem<GunItem>) h)
                .toList();
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return ItemRegistry.ITEMS.getEntries().stream().map(holder -> holder);
    }
}
