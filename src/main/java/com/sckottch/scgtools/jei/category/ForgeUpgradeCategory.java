package com.sckottch.scgtools.jei.category;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.forge.operation.ForgeOperation;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.jei.ForgeStacks;
import com.sckottch.scgtools.jei.display.ForgeUpgradeDisplay;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class ForgeUpgradeCategory extends AbstractForgeCategory<ForgeUpgradeDisplay> {
    public static final IRecipeType<ForgeUpgradeDisplay> TYPE =
        IRecipeType.create(SCGTools.MOD_ID, "forge_upgrades", ForgeUpgradeDisplay.class);

    public ForgeUpgradeCategory(IGuiHelper guiHelper) {
        super(TYPE,
            Component.translatable("gui.scgtools.jei.forge_upgrades"),
            guiHelper.createDrawableItemLike(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            guiHelper
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ForgeUpgradeDisplay display, IFocusGroup focuses) {
        addMaterialSlots(builder, display.materials());

        ToolUpgrade upgrade = display.upgrade();
        List<ItemStack> inputs = ForgeStacks.toolsFromTier(upgrade.getApplicableTo(), upgrade.getMinTier());
        List<ItemStack> outputs = ForgeStacks.applyOperation(inputs, operation(display));

        IRecipeSlotBuilder input = builder.addInputSlot(MAIN_X, MAIN_Y)
            .setSlotName(INPUT_SLOT)
            .addItemStacks(inputs);

        IRecipeSlotBuilder output = builder.addOutputSlot(OUTPUT_X, OUTPUT_Y)
            .setSlotName(OUTPUT_SLOT)
            .addItemStacks(outputs);

        // lança IllegalArgumentException se os slots tiverem contagens diferentes
        builder.createFocusLink(input, output);
    }

    @Override
    public void onDisplayedIngredientsUpdate(ForgeUpgradeDisplay display, List<IRecipeSlotDrawable> slots, IFocusGroup focuses) {
        syncOutput(slots, operation(display));
    }

    @Override
    public void draw(ForgeUpgradeDisplay display, IRecipeSlotsView slotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        drawForge(graphics, display.xpCost());
    }

    @Override
    public Identifier getIdentifier(ForgeUpgradeDisplay display) {
        return display.id();
    }

    private static ForgeOperation operation(ForgeUpgradeDisplay display) {
        return display.holder().value().getOperation();
    }
}
