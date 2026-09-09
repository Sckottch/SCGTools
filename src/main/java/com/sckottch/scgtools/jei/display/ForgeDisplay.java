package com.sckottch.scgtools.jei.display;

import com.sckottch.scgtools.forge.operation.TierUpOperation;
import com.sckottch.scgtools.recipe.SimpleForgeRecipe;
import com.sckottch.scgtools.recipe.ToolForgeRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public sealed interface ForgeDisplay {
    RecipeHolder<?> holder();
    List<SizedIngredient> materials();
    int xpCost();

    default Identifier id() {
        return holder().id().identifier();
    }

    record Simple(RecipeHolder<SimpleForgeRecipe> holder) implements ForgeDisplay {
        @Override
        public List<SizedIngredient> materials() {
            return holder.value().getIngredients();
        }

        @Override
        public int xpCost() {
            return holder.value().getXpCost();
        }
    }

    record TierUp(RecipeHolder<ToolForgeRecipe> holder, TierUpOperation operation, Item tool) implements ForgeDisplay {
        @Override
        public List<SizedIngredient> materials() {
            return holder.value().getIngredients();
        }

        @Override
        public int xpCost() {
            return holder.value().getXpCost();
        }
    }
}
