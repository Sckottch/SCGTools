package com.sckottch.scgtools.jei;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.forge.operation.ApplyUpgradeOperation;
import com.sckottch.scgtools.forge.operation.TierUpOperation;
import com.sckottch.scgtools.item.tools.EvolvingTool;
import com.sckottch.scgtools.jei.category.ForgeRecipeCategory;
import com.sckottch.scgtools.jei.category.ForgeUpgradeCategory;
import com.sckottch.scgtools.jei.display.ForgeDisplay;
import com.sckottch.scgtools.jei.display.ForgeUpgradeDisplay;
import com.sckottch.scgtools.recipe.SimpleForgeRecipe;
import com.sckottch.scgtools.recipe.ToolForgeRecipe;
import com.sckottch.scgtools.utility.events.ModRecipeSyncClient;
import com.sckottch.scgtools.utility.tags.ModTags;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class SCGToolsJeiPlugin implements IModPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(SCGTools.MOD_ID, "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof EvolvingTool) {
                registration.registerFromDataComponentTypes(item, ModDataComponents.TOOL_TIER.get());
            }
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(
            new ForgeRecipeCategory(guiHelper),
            new ForgeUpgradeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<ForgeDisplay> recipes = new ArrayList<>();
        List<ForgeUpgradeDisplay> upgrades = new ArrayList<>();

        for (RecipeHolder<SimpleForgeRecipe> holder : ModRecipeSyncClient.getSimpleRecipes()) {
            recipes.add(new ForgeDisplay.Simple(holder));
        }

        List<Item> evolvingTools = ForgeStacks.toolsIn(ModTags.Items.EVOLVING_ITEM);

        for (RecipeHolder<ToolForgeRecipe> holder : ModRecipeSyncClient.getToolRecipes()) {
            switch (holder.value().getOperation()) {
                case TierUpOperation tierUp -> {
                    for (Item tool : evolvingTools) {
                        recipes.add(new ForgeDisplay.TierUp(holder, tierUp, tool));
                    }
                }
                case ApplyUpgradeOperation apply -> upgrades.add(new ForgeUpgradeDisplay(holder, apply.upgrade()));
                default -> SCGTools.LOGGER.warn("No JEI category for forge recipe {} with operation {}",
                    holder.id().identifier(), holder.value().getOperation().getClass().getSimpleName());
            }
        }

        if (evolvingTools.isEmpty()) {
            SCGTools.LOGGER.warn("JEI: tag {} is empty, no tier-up recipes will be shown",
                ModTags.Items.EVOLVING_ITEM.location());
        }

        registration.addRecipes(ForgeRecipeCategory.TYPE, recipes);
        registration.addRecipes(ForgeUpgradeCategory.TYPE, upgrades);

        SCGTools.LOGGER.info("JEI: registered {} forge recipes, {} forge upgrades", recipes.size(), upgrades.size());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(ForgeRecipeCategory.TYPE, ModBlocks.FORGE.get());
        registration.addCraftingStation(ForgeUpgradeCategory.TYPE, ModBlocks.FORGE.get());
    }
}
