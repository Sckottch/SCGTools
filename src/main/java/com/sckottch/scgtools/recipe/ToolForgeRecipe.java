package com.sckottch.scgtools.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sckottch.scgtools.forge.operation.ForgeOperation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public class ToolForgeRecipe implements Recipe<ForgeRecipeInput> {
    
    private final List<SizedIngredient> ingredients;
    private final Ingredient tool;
    private final int xpCost;
    private final ForgeOperation operation;
    
    public ToolForgeRecipe(List<SizedIngredient> ingredients, Ingredient tool,
                           int xpCost, ForgeOperation operation) {
        this.ingredients = ingredients;
        this.tool = tool;
        this.xpCost = xpCost;
        this.operation = operation;
    }
    
    public static final MapCodec<ToolForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        SizedIngredient.NESTED_CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.ingredients),
        Ingredient.CODEC.fieldOf("tool").forGetter(r -> r.tool),
        Codec.INT.optionalFieldOf("xp_cost", 0).forGetter(r -> r.xpCost),
        ForgeOperation.CODEC.fieldOf("operation").forGetter(r -> r.operation)
    ).apply(inst, ToolForgeRecipe::new));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolForgeRecipe> STREAM_CODEC =
        StreamCodec.composite(
            SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.tool,
            ByteBufCodecs.INT, r -> r.xpCost,
            ForgeOperation.STREAM_CODEC, r -> r.operation,
            ToolForgeRecipe::new
        );
    
    @Override
    public boolean matches(ForgeRecipeInput input, Level level) {
        return matchesInputs(input) && operation.canApply(input.main());
    }

    // separado de matches para a forja identificar a receita pretendida mesmo quando a
    // operação não pode ser aplicada, e assim conseguir dizer o motivo na tela
    public boolean matchesInputs(ForgeRecipeInput input) {
        return tool.test(input.main()) && matchesIngredients(input.ingredients());
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
    public ItemStack assemble(ForgeRecipeInput input) {
        return input.main().copy();
    }
    
    @Override
    public boolean isSpecial() {
        return true;
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
    public RecipeType<? extends Recipe<ForgeRecipeInput>> getType() {
        return ModRecipes.FORGE_TOOL_TYPE.get();
    }
    
    @Override
    public PlacementInfo placementInfo() {
        return null;
    }
    
    @Override
    public RecipeBookCategory recipeBookCategory() {
        return null;
    }
    
    @Override
    public RecipeSerializer<? extends Recipe<ForgeRecipeInput>> getSerializer() {
        return ModRecipes.FORGE_TOOL_SERIALIZER.get();
    }
    
    public ForgeOperation getOperation() { return operation; }
    public int getXpCost() { return xpCost; }
    public List<SizedIngredient> getIngredients() { return ingredients; }
}