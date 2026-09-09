package com.sckottch.scgtools.jei.category;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.jei.ForgeStacks;
import com.sckottch.scgtools.jei.display.ForgeDisplay;
import com.sckottch.scgtools.recipe.SimpleForgeRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class ForgeRecipeCategory extends AbstractForgeCategory<ForgeDisplay> {
    public static final IRecipeType<ForgeDisplay> TYPE =
        IRecipeType.create(SCGTools.MOD_ID, "forge_recipes", ForgeDisplay.class);

    public ForgeRecipeCategory(IGuiHelper guiHelper) {
        super(TYPE,
            Component.translatable("gui.scgtools.jei.forge_recipes"),
            guiHelper.createDrawableItemLike(ModBlocks.FORGE.get()),
            guiHelper
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ForgeDisplay display, IFocusGroup focuses) {
        addMaterialSlots(builder, display.materials());

        switch (display) {
            case ForgeDisplay.Simple simple -> setSimpleRecipe(builder, simple);
            case ForgeDisplay.TierUp tierUp -> setTierUpRecipe(builder, tierUp);
        }
    }

    private void setSimpleRecipe(IRecipeLayoutBuilder builder, ForgeDisplay.Simple simple) {
        SimpleForgeRecipe recipe = simple.holder().value();

        builder.addInputSlot(MAIN_X, MAIN_Y).add(recipe.getMainItem());
        builder.addOutputSlot(OUTPUT_X, OUTPUT_Y).add(recipe.getResult().create());
    }

    private void setTierUpRecipe(IRecipeLayoutBuilder builder, ForgeDisplay.TierUp tierUp) {
        ItemStack input = ForgeStacks.toolAtTier(tierUp.tool(), tierUp.operation().from());

        builder.addInputSlot(MAIN_X, MAIN_Y).add(input);
        builder.addOutputSlot(OUTPUT_X, OUTPUT_Y).add(ForgeStacks.applyOperation(input, tierUp.operation()));
    }

    @Override
    public void draw(ForgeDisplay display, IRecipeSlotsView slotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        drawForge(graphics, display.xpCost());
    }

    @Override
    public Identifier getIdentifier(ForgeDisplay display) {
        return display.id();
    }
}
