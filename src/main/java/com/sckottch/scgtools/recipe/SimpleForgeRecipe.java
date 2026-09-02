package com.sckottch.scgtools.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class SimpleForgeRecipe implements Recipe<ForgeRecipeInput> {
    private final List<SizedIngredient> ingredients;
    private final Ingredient mainItem;
    private final int xpCost;
    private final ItemStackTemplate result;
    
    public SimpleForgeRecipe(List<SizedIngredient> ingredients, Ingredient mainItem, int xpCost, ItemStackTemplate result) {
        this.ingredients = ingredients;
        this.mainItem = mainItem;
        this.xpCost = xpCost;
        this.result = result;
    }
    
    @Override
    public boolean matches(ForgeRecipeInput forgeRecipeInput, Level level) {
        if (!mainItem.test(forgeRecipeInput.main())) return false;
        return matchesIngredients(forgeRecipeInput.ingredients());
    }
    
    private boolean matchesIngredients(List<ItemStack> slots) {
        int[] available = new int[slots.size()];
        for (int i = 0; i < slots.size(); i++) {
            available[i] = slots.get(i).getCount();
        }
        
        for (SizedIngredient required : ingredients) {
            int remaining = required.count();
            
            for (int i = 0; i < slots.size() && remaining > 0; i++) {
                if (available[i] <= 0) continue;
                if (!required.ingredient().test(slots.get(i))) continue;
                
                int taken = Math.min(available[i], remaining);
                available[i] -= taken;
                remaining -= taken;
            }
            
            if (remaining > 0) return false;
        }
        
        return true;
    }
    
    @Override
    public ItemStack assemble(ForgeRecipeInput forgeRecipeInput) {
        return result.create();
    }
    
    @Override
    public boolean isSpecial() {
        return true;
    }
    
    @Override
    public RecipeType<? extends Recipe<ForgeRecipeInput>> getType() {
        return ModRecipes.FORGE_SIMPLE_TYPE.get();
    }
    
    @Override
    public RecipeSerializer<? extends Recipe<ForgeRecipeInput>> getSerializer() {
        return ModRecipes.FORGE_SIMPLE_SERIALIZER.get();
    }
    
    public int getXpCost() {
        return xpCost;
    }
    
    public Ingredient getMainItem() {
        return mainItem;
    }
    
    public List<SizedIngredient> getIngredients() {
        return ingredients;
    }
    
    public ItemStackTemplate getResult() {
        return result;
    }
    
    @Override
    public boolean showNotification() {
        return false;
    }
    
    @Override
    public String group() {
        return "";
    }
    
    @Override
    public @Nullable PlacementInfo placementInfo() {
        return null;
    }
    
    @Override
    public @Nullable RecipeBookCategory recipeBookCategory() {
        return null;
    }
    
    public static final MapCodec<SimpleForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        SizedIngredient.NESTED_CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.ingredients),
        Ingredient.CODEC.fieldOf("main").forGetter(r -> r.mainItem),
        Codec.INT.optionalFieldOf("xp_cost", 0).forGetter(r -> r.xpCost),
        ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.result)
    ).apply(inst, SimpleForgeRecipe::new));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleForgeRecipe> STREAM_CODEC =
        StreamCodec.composite(
            SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.mainItem,
            ByteBufCodecs.INT, r -> r.xpCost,
            ItemStackTemplate.STREAM_CODEC, r -> r.result,
            SimpleForgeRecipe::new
        );
}
