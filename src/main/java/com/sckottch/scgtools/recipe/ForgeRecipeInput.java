package com.sckottch.scgtools.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record ForgeRecipeInput(List<ItemStack> ingredients, ItemStack main) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        if (slot < ingredients.size()) return ingredients.get(slot);
        if(slot == ingredients.size()) return main;
        throw new IllegalArgumentException("No item for index " + slot);
    }
    
    @Override
    public int size() {
        return ingredients.size() + 1;
    }
}
