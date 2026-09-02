package com.sckottch.scgtools.recipe;

import com.sckottch.scgtools.SCGTools;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
        DeferredRegister.create(Registries.RECIPE_TYPE, SCGTools.MODID);
    
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, SCGTools.MODID);
    
    public static final Supplier<RecipeType<SimpleForgeRecipe>> FORGE_SIMPLE_TYPE =
        RECIPE_TYPES.register("forge_simple", RecipeType::simple);
    
    public static final Supplier<RecipeSerializer<SimpleForgeRecipe>> FORGE_SIMPLE_SERIALIZER =
        RECIPE_SERIALIZERS.register("forge_simple",
            () -> new RecipeSerializer<>(SimpleForgeRecipe.CODEC, SimpleForgeRecipe.STREAM_CODEC));
    
    public static final Supplier<RecipeType<ToolForgeRecipe>> FORGE_TOOL_TYPE =
        RECIPE_TYPES.register("forge_tool", RecipeType::simple);
    
    public static final Supplier<RecipeSerializer<ToolForgeRecipe>> FORGE_TOOL_SERIALIZER =
        RECIPE_SERIALIZERS.register("forge_tool",
            () -> new RecipeSerializer<>(ToolForgeRecipe.CODEC, ToolForgeRecipe.STREAM_CODEC));
    
    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
