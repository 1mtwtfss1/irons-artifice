package io.redspace.irons_artifice.datagen;

import com.geckolib.renderer.internal.GeckolibItemSpecialRenderer;
import io.redspace.irons_artifice.IronsArtifice;
import io.redspace.irons_artifice.item.GunItem;
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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;
import java.util.stream.Stream;

public class ItemModelDataGenerator extends ModelProvider {
    private static final Identifier GECKOLIB_GUN_DISPLAY = IronsArtifice.id("item/gun_display");
    private static final Identifier REVOLVER_GUN_DISPLAY = IronsArtifice.id("item/pistol_display");
    private static final Identifier DEMO_GUN_MODEL = IronsArtifice.id("item/gun");

    public ItemModelDataGenerator(PackOutput output) {
        super(output, IronsArtifice.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        for (var item : ItemRegistry.ITEMS.getEntries()) {
            if (item.get() instanceof GunItem) {
                Identifier displayParent = GECKOLIB_GUN_DISPLAY;
                if (item == ItemRegistry.BLACKPOWDER_REVOLVER || item == ItemRegistry.SIX_SHOOTER) {
                    displayParent = REVOLVER_GUN_DISPLAY;
                }
                itemModels.itemModelOutput.accept(
                        item.get(),
                        ItemModelUtils.specialModel(displayParent, new GeckolibItemSpecialRenderer.Unbaked<>())
                );
            } else {
                generateTemplatedItem(itemModels, item.get(), itemTexture(item));
            }
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

    private static Identifier itemTexture(DeferredHolder<?, ?> item) {
        return itemTexture(item.getId());
    }

    private static Identifier itemTexture(Identifier identifier) {
        return identifier.withPrefix("item/");
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
