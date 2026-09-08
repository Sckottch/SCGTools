package com.sckottch.scgtools.forge;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.utility.enums.UpgradeValidationResult;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;


public class ForgeScreen extends AbstractContainerScreen<ForgeMenu> {
    public static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(SCGTools.MOD_ID, "textures/gui/forge.png");

    private static final Identifier ERROR_SPRITE = Identifier.withDefaultNamespace("container/smithing/error");

    private static final int ARROW_CENTER_X = 120;
    private static final int XP_LABEL_Y = 20;
    private static final int XP_ENOUGH = 0xFF80FF20;
    private static final int XP_MISSING = 0xFFFF6060;

    private static final int ERROR_ICON_WIDTH = 28;
    private static final int ERROR_ICON_HEIGHT = 21;
    private static final int ERROR_ICON_X = ARROW_CENTER_X - ERROR_ICON_WIDTH / 2;
    private static final int ERROR_ICON_Y = 28;

    private static final int MESSAGE_RIGHT = 168;
    private static final int MESSAGE_Y = 72;

    public ForgeScreen(ForgeMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        if (!menu.getValidation().isOk()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ERROR_SPRITE,
                x + ERROR_ICON_X, y + ERROR_ICON_Y, ERROR_ICON_WIDTH, ERROR_ICON_HEIGHT
            );
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, -12566464, false);

        UpgradeValidationResult validation = menu.getValidation();
        Component message = validation.getMessage();

        if (message != null) {
            graphics.text(font, message, MESSAGE_RIGHT - font.width(message), MESSAGE_Y, XP_MISSING, false);
            return;
        }

        int cost = menu.getXpCost();
        if (cost <= 0) return;

        graphics.centeredText(font, String.valueOf(cost), ARROW_CENTER_X, XP_LABEL_Y,
            menu.canAffordXp() ? XP_ENOUGH : XP_MISSING
        );
    }
}
