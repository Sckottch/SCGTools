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
}
