package com.sckottch.scgtools.forge;

import com.sckottch.scgtools.SCGTools;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;


public class ForgeScreen extends AbstractContainerScreen<ForgeMenu> {
    public static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(SCGTools.MODID, "textures/gui/forge.png");

    private static final int ARROW_CENTER_X = 120;
    private static final int XP_LABEL_Y = 20;
    private static final int XP_ENOUGH = 0xFF80FF20;
    private static final int XP_MISSING = 0xFFFF6060;

    public ForgeScreen(ForgeMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, -12566464, false);

        int cost = menu.getXpCost();
        if (cost <= 0) return;

        graphics.centeredText(font, String.valueOf(cost), ARROW_CENTER_X, XP_LABEL_Y,
            menu.canAffordXp() ? XP_ENOUGH : XP_MISSING
        );
    }
}
