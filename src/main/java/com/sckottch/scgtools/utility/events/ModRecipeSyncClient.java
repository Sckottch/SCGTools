package com.sckottch.scgtools.utility.events;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.forge.operation.ApplyUpgradeOperation;
import com.sckottch.scgtools.forge.operation.ForgeOperation;
import com.sckottch.scgtools.forge.operation.TierUpOperation;
import com.sckottch.scgtools.recipe.ModRecipes;
import com.sckottch.scgtools.recipe.SimpleForgeRecipe;
import com.sckottch.scgtools.recipe.ToolForgeRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.List;

@EventBusSubscriber(modid = SCGTools.MOD_ID, value = Dist.CLIENT)
public class ModRecipeSyncClient {
    private static List<RecipeHolder<SimpleForgeRecipe>> simpleRecipes = List.of();
    private static List<RecipeHolder<ToolForgeRecipe>> toolRecipes = List.of();

    public static List<RecipeHolder<SimpleForgeRecipe>> getSimpleRecipes() {
        return simpleRecipes;
    }

    public static List<RecipeHolder<ToolForgeRecipe>> getToolRecipes() {
        return toolRecipes;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        RecipeMap map = event.getRecipeMap();

        simpleRecipes = List.copyOf(map.byType(ModRecipes.FORGE_SIMPLE_TYPE.get()));
        toolRecipes = List.copyOf(map.byType(ModRecipes.FORGE_TOOL_TYPE.get()));

        logSummary();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        simpleRecipes = List.of();
        toolRecipes = List.of();
    }

    private static void logSummary() {
        int tierUp = 0;
        int upgrade = 0;
        int other = 0;

        for (RecipeHolder<ToolForgeRecipe> holder : toolRecipes) {
            ForgeOperation operation = holder.value().getOperation();

            if (operation instanceof TierUpOperation) tierUp++;
            else if (operation instanceof ApplyUpgradeOperation) upgrade++;
            else other++;
        }

        SCGTools.LOGGER.debug("Forge recipes received: {} simple, {} tool ({} tier-up, {} upgrade, {} other)",
            simpleRecipes.size(), toolRecipes.size(), tierUp, upgrade, other);
    }
}
