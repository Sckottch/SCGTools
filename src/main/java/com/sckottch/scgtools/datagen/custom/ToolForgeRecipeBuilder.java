package com.sckottch.scgtools.datagen.custom;

import com.sckottch.scgtools.forge.operation.ForgeOperation;
import com.sckottch.scgtools.recipe.ToolForgeRecipe;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToolForgeRecipeBuilder implements RecipeBuilder {
    
    private final List<SizedIngredient> ingredients = new ArrayList<>();
    private final Ingredient tool;
    private final ForgeOperation operation;
    private int xpCost = 0;
    
    public ToolForgeRecipeBuilder(Ingredient tool, ForgeOperation operation) {
        this.tool = tool;
        this.operation = operation;
    }
    
    public ToolForgeRecipeBuilder ingredient(SizedIngredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }
    
    public ToolForgeRecipeBuilder ingredient(ItemLike item, int count) {
        return ingredient(new SizedIngredient(Ingredient.of(item), count));
    }
    
    public ToolForgeRecipeBuilder xpCost(int xp) {
        this.xpCost = xp;
        return this;
    }
    
    @Override
    public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
        return null;
    }
    
    @Override
    public RecipeBuilder group(@Nullable String s) {
        return null;
    }
    
    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE, operation.defaultRecipeId());
    }
    
    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> key) {
        output.accept(key, new ToolForgeRecipe(
            List.copyOf(ingredients), tool, xpCost, operation), null);
    }
}