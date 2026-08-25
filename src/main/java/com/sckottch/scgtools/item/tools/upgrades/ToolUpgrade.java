package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.world.item.ItemStack;

public abstract class ToolUpgrade {
    private final ToolTier minTier;
    private final String displayName;
    
    protected ToolUpgrade(ToolTier minTier, String displayName) {
        this.minTier = minTier;
        this.displayName = displayName;
    }
    
    public ToolTier getMinTier() {
        return minTier;
    }
    public String getDisplayName() {
        return displayName;
    }
    
    public void onApply(ItemStack stack) {}
    public void onRemove(ItemStack stack) {}
    
    public boolean canApplyTo(ToolTier toolTier) {
        return toolTier.getId() >= minTier.getId();
    }
}
