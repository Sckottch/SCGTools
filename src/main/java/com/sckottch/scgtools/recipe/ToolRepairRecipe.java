package com.sckottch.scgtools.recipe;

import com.mojang.serialization.MapCodec;
import com.sckottch.scgtools.item.tools.EvolvingTool;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class ToolRepairRecipe extends CustomRecipe {
    public static final ToolRepairRecipe INSTANCE = new ToolRepairRecipe();
    public static final MapCodec<ToolRepairRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolRepairRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<ToolRepairRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
    
    private static final int UNITS_FOR_FULL = 3;
    
    private record Parsed(ItemStack tool, int materials) {}
    
    private static @Nullable Parsed parse(CraftingInput input) {
        ItemStack tool = ItemStack.EMPTY;
        int materials = 0;
        
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            
            if (stack.getItem() instanceof EvolvingTool) {
                if (!tool.isEmpty()) return null;
                if (stack.getCount() != 1) return null;
                tool = stack;
            } else {
                materials++;
            }
        }
        
        if (tool.isEmpty()) return null;
        if (materials == 0 || materials > UNITS_FOR_FULL) return null;
        if (!tool.isDamageableItem() || tool.getDamageValue() == 0) return null;
        
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty() || stack.getItem() instanceof EvolvingTool) continue;
            if (!tool.isValidRepairItem(stack)) return null;
        }
        
        return new Parsed(tool, materials);
    }
    
    @Override
    public boolean matches(CraftingInput input, Level level) {
        return parse(input) != null;
    }
    
    @Override
    public ItemStack assemble(CraftingInput input) {
        Parsed parsed = parse(input);
        if (parsed == null) return ItemStack.EMPTY;
        
        ItemStack result = parsed.tool().copy();
        int perUnit = Mth.ceil(result.getMaxDamage() / (float) UNITS_FOR_FULL);
        result.setDamageValue(result.getDamageValue() - perUnit * parsed.materials());
        
        return result;
    }
    
    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }
}