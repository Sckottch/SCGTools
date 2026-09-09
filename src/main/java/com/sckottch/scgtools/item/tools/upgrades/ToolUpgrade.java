package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.components.ToolLevelData;
import com.sckottch.scgtools.item.tools.EvolvingTool;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.enums.UpgradeValidationResult;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class ToolUpgrade {
    private final ToolTier minTier;
    private final String displayName;
    private final TagKey<Item> applicableTo;
    
    protected ToolUpgrade(ToolTier minTier, String displayName, TagKey<Item> applicableTo) {
        this.minTier = minTier;
        this.displayName = displayName;
        this.applicableTo = applicableTo;
    }
    
    public ToolTier getMinTier() {
        return minTier;
    }
    public String getDisplayName() {
        return displayName;
    }
    public TagKey<Item> getApplicableTo() {
        return applicableTo;
    }
    
    protected void onApply(ItemStack stack) {}
//    public void onRemove(ItemStack stack) {}
    
    private UpgradeValidationResult canApplyTo(ItemStack stack, ToolTier toolTier) {
        if (!stack.is(applicableTo)) return UpgradeValidationResult.WRONG_TOOL;
        if (toolTier.getId() < minTier.getId()) return UpgradeValidationResult.WRONG_TIER;
        
        return UpgradeValidationResult.OK;
    }
    
    public UpgradeValidationResult validate(ItemStack stack) {
        if (!(stack.getItem() instanceof EvolvingTool)) {
            return UpgradeValidationResult.INVALID;
        }
        
        ToolTier tier  = stack.get(ModDataComponents.TOOL_TIER.get());
        ToolLevelData level  = stack.get(ModDataComponents.TOOL_LEVEL.get());
        List<ToolUpgrade> current = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        
        if (tier == null || level == null || current == null) {
            return UpgradeValidationResult.INVALID;
        }
        
        UpgradeValidationResult canApply = this.canApplyTo(stack, tier);
        
        if (!(canApply == UpgradeValidationResult.OK)) return canApply;
        
        if (current.contains(this)) {
            return UpgradeValidationResult.ALREADY_APPLIED;
        }
        
        if (!(current.size() < level.slotCount())) {
            return UpgradeValidationResult.NO_SLOTS;
        }
        
        return UpgradeValidationResult.OK;
    }
    
    public void applyTo(ItemStack stack) {
        List<ToolUpgrade> current = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (current == null) return;
        
        List<ToolUpgrade> updated = new ArrayList<>(current);
        updated.add(this);
        
        stack.set(ModDataComponents.TOOL_UPGRADES.get(), List.copyOf(updated));
        this.onApply(stack);
    }
}
