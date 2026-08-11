package io.redspace.irons_artifice.datagen;

import io.redspace.irons_artifice.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class ItemTagDataGenerator extends IntrinsicHolderTagsProvider<Item> {
    public ItemTagDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ITEM, lookupProvider, item -> item.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        this.tag(ItemTags.HEAD_ARMOR)
                .add(ItemRegistry.COWBOY_HAT.get())
                .add(ItemRegistry.TRICORNE_HAT.get())
        ;
    }
}
