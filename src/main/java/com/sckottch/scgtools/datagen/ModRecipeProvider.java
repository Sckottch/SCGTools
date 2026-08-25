package com.sckottch.scgtools.datagen;

import com.sckottch.scgtools.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }
    
    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.TOOLS, ModItems.PICKAXE)
            .pattern("FFF")
            .pattern(" S ")
            .pattern(" S ")
            .define('F', Items.FLINT)
            .define('S', Items.STICK)
            .unlockedBy("has_flint", this.has(Items.FLINT))
            .save(this.output);
    }
    
    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, lookup);
        }
        
        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new ModRecipeProvider(provider, recipeOutput);
        }
        
        @Override
        public String getName() {
            return "SCG Tools Recipes";
        }
    }
}
