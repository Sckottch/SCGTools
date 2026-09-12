package com.sckottch.scgtools.jei.category;

import com.sckottch.scgtools.forge.ForgeBlockEntity;
import com.sckottch.scgtools.forge.ForgeScreen;
import com.sckottch.scgtools.forge.operation.ForgeOperation;
import com.sckottch.scgtools.jei.ForgeStacks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public abstract class AbstractForgeCategory<T> extends AbstractRecipeCategory<T> {
    public static final int WIDTH = 170;
    public static final int HEIGHT = 63;

    private static final int CROP_U = 4;
    private static final int CROP_V = 7;

    private static final int MATERIAL_X = 4;
    private static final int MATERIAL_Y = 5;
    private static final int MATERIAL_STEP = 18;
    private static final int MATERIAL_COLUMNS = 2;

    private static final int MAIN_X = 68;
    private static final int MAIN_Y = 23;
    private static final int OUTPUT_X = 148;
    private static final int OUTPUT_Y = 23;

    private static final int XP_CENTER_X = 116;
    private static final int XP_Y = 13;
    private static final int XP_COLOR = 0xFF80FF20;

    private static final int HEADER_TEXT_Y = 2;
    private static final int HEADER_COLOR = 0xFF404040;

    protected static final String INPUT_SLOT = "main_input";
    protected static final String OUTPUT_SLOT = "main_output";

    private final IDrawable background;
    private final int headerHeight;

    protected AbstractForgeCategory(IRecipeType<T> recipeType, Component title, IDrawable icon, IGuiHelper guiHelper) {
        this(recipeType, title, icon, guiHelper, 0);
    }

    // headerHeight reserva espaço acima da forja para um título por receita; toda a
    // arte e os slots são deslocados para baixo por esse valor
    protected AbstractForgeCategory(IRecipeType<T> recipeType, Component title, IDrawable icon, IGuiHelper guiHelper, int headerHeight) {
        super(recipeType, title, icon, WIDTH, HEIGHT + headerHeight);

        this.background = guiHelper.createDrawable(ForgeScreen.TEXTURE, CROP_U, CROP_V, WIDTH, HEIGHT);
        this.headerHeight = headerHeight;
    }

    protected void addMaterialSlots(IRecipeLayoutBuilder builder, List<SizedIngredient> materials) {
        int count = Math.min(materials.size(), ForgeBlockEntity.RECIPE_SLOTS);

        for (int i = 0; i < count; i++) {
            int x = MATERIAL_X + (i % MATERIAL_COLUMNS) * MATERIAL_STEP;
            int y = MATERIAL_Y + (i / MATERIAL_COLUMNS) * MATERIAL_STEP + headerHeight;

            IRecipeSlotBuilder slot = builder.addInputSlot(x, y);
            slot.addItemStacks(ForgeStacks.materialStacks(materials.get(i), slot.getContextMap()));
        }
    }

    protected IRecipeSlotBuilder addMainInputSlot(IRecipeLayoutBuilder builder) {
        return builder.addInputSlot(MAIN_X, MAIN_Y + headerHeight);
    }

    protected IRecipeSlotBuilder addMainOutputSlot(IRecipeLayoutBuilder builder) {
        return builder.addOutputSlot(OUTPUT_X, OUTPUT_Y + headerHeight);
    }

    protected void drawForge(GuiGraphicsExtractor graphics, int xpCost) {
        background.draw(graphics, 0, headerHeight);

        if (xpCost <= 0) return;

        graphics.centeredText(Minecraft.getInstance().font, String.valueOf(xpCost), XP_CENTER_X, XP_Y + headerHeight, XP_COLOR);
    }

    protected void drawHeader(GuiGraphicsExtractor graphics, Component text) {
        Font font = Minecraft.getInstance().font;
        int x = (WIDTH - font.width(text)) / 2;

        graphics.text(font, text, x, HEADER_TEXT_Y, HEADER_COLOR, false);
    }

    protected void syncOutput(List<IRecipeSlotDrawable> slots, ForgeOperation operation) {
        IRecipeSlotDrawable input = findSlot(slots, INPUT_SLOT);
        IRecipeSlotDrawable output = findSlot(slots, OUTPUT_SLOT);

        if (input == null || output == null) return;

        Optional<ItemStack> displayed = input.getDisplayedIngredient(VanillaTypes.ITEM_STACK);
        output.clearDisplayOverrides();

        if (displayed.isEmpty() || ForgeStacks.lookup().isEmpty()) return;

        output.createDisplayOverrides().add(ForgeStacks.applyOperation(displayed.get(), operation));
    }

    private static @Nullable IRecipeSlotDrawable findSlot(List<IRecipeSlotDrawable> slots, String name) {
        for (IRecipeSlotDrawable slot : slots) {
            if (slot.getSlotName().map(name::equals).orElse(false)) return slot;
        }

        return null;
    }
}
