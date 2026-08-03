package io.redspace.irons_artifice.datagen;

import io.redspace.irons_artifice.IronsArtifice;
import io.redspace.irons_artifice.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class RecipeDataGenerator extends RecipeProvider {
    protected RecipeDataGenerator(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemRegistry.BLACKPOWDER.get(), 6)
                .requires(Items.GUNPOWDER)
                .requires(Items.CHARCOAL)
                .requires(Items.REDSTONE)
                .unlockedBy("has_gunpowder", this.has(Items.GUNPOWDER))
                .unlockedBy("has_charcoal", this.has(Items.CHARCOAL))
                .unlockedBy("has_redstone", this.has(Items.REDSTONE))
                .save(this.output, recipeId("blackpowder_from_gunpowder"));
        ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemRegistry.BLACKPOWDER.get(), 2)
                .requires(Items.CHARCOAL)
                .requires(Items.REDSTONE)
                .unlockedBy("has_charcoal", this.has(Items.CHARCOAL))
                .unlockedBy("has_redstone", this.has(Items.REDSTONE))
                .save(this.output, recipeId("blackpowder"));
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemRegistry.BULLET.get(), 16)
                .pattern("#")
                .pattern("^")
                .define('#', TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/iron")))
                .define('^', ItemRegistry.BLACKPOWDER.get())
                .unlockedBy("has_blackpowder", this.has(ItemRegistry.BLACKPOWDER))
                .save(this.output, recipeId("bullet_from_iron"));
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemRegistry.BULLET.get(), 4)
                .pattern("#")
                .pattern("^")
                .define('#', TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/copper")))
                .define('^', ItemRegistry.BLACKPOWDER.get())
                .unlockedBy("has_blackpowder", this.has(ItemRegistry.BLACKPOWDER))
                .save(this.output, recipeId("bullet_from_copper"));

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemRegistry.SIMPLE_MECHANICAL_COMPONENTS.get())
                .pattern("CNC")
                .pattern("IRI")
                .pattern("NCN")
                .define('N', TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "nuggets/copper")))
                .define('C', Items.COPPER_CHAIN.unaffected())
                .define('I', TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/copper")))
                .define('R', Items.REDSTONE)
                .unlockedBy("has_copper_nugget", this.has(Items.COPPER_NUGGET))
                .unlockedBy("has_redstone", this.has(Items.REDSTONE))
                .save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemRegistry.MECHANICAL_COMPONENTS.get())
                .pattern("BC ")
                .pattern("CMC")
                .pattern(" CN")
                .define('B', TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/iron")))
                .define('N', TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "nuggets/iron")))
                .define('C', Items.IRON_CHAIN)
                .define('M', ItemRegistry.SIMPLE_MECHANICAL_COMPONENTS)
                .unlockedBy("has_simple", this.has(ItemRegistry.SIMPLE_MECHANICAL_COMPONENTS))
                .save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemRegistry.CLOCKWORK_COMPONENTS.get())
                .pattern("MI ")
                .pattern("IRI")
                .pattern(" IM")
                .define('I', TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/gold")))
                .define('R', Items.REDSTONE)
                .define('M', ItemRegistry.MECHANICAL_COMPONENTS)
                .unlockedBy("has_mechanical", this.has(ItemRegistry.MECHANICAL_COMPONENTS))
                .save(this.output);
    }

    private static ResourceKey<Recipe<?>> recipeId(Identifier identifier) {
        return ResourceKey.create(Registries.RECIPE, identifier);
    }

    private static ResourceKey<Recipe<?>> recipeId(String name) {
        return ResourceKey.create(Registries.RECIPE, IronsArtifice.id(name));
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipeDataGenerator(registries, output);
        }

        @Override
        public String getName() {
            return IronsArtifice.MODID + "_recipes";
        }
    }
}
