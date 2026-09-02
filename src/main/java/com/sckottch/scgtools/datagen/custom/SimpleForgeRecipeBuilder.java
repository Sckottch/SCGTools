package com.sckottch.scgtools.datagen.custom;

import com.sckottch.scgtools.recipe.SimpleForgeRecipe;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SimpleForgeRecipeBuilder implements RecipeBuilder {
    private final List<SizedIngredient> ingredients = new ArrayList<>();
    private final Ingredient mainItem;
    private final ItemStackTemplate result;
    private int xpCost = 0;
    
    public SimpleForgeRecipeBuilder(ItemStackTemplate result, Ingredient mainItem) {
        this.result = result;
        this.mainItem = mainItem;
    }
    
    public SimpleForgeRecipeBuilder ingredient(SizedIngredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }
    
    public SimpleForgeRecipeBuilder xpCost(int xp) {
        this.xpCost = xp;
        return this;
    }
    
    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> key) {
        SimpleForgeRecipe recipe = new SimpleForgeRecipe(
            List.copyOf(ingredients), mainItem, xpCost, result
        );
        output.accept(key, recipe, null);
    }
    
    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }
    
    @Override
    public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
        return null;
    }
    
    @Override
    public RecipeBuilder group(@Nullable String s) {
        return null;
    }
}
