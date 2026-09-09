package com.sckottch.scgtools.jei.display;

import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.recipe.ToolForgeRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public record ForgeUpgradeDisplay(RecipeHolder<ToolForgeRecipe> holder, ToolUpgrade upgrade) {
    public List<SizedIngredient> materials() {
        return holder.value().getIngredients();
    }

    public int xpCost() {
        return holder.value().getXpCost();
    }

    public Identifier id() {
        return holder.id().identifier();
    }
}
