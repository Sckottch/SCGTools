package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.utility.enums.HammerMode;
import com.sckottch.scgtools.utility.enums.MiningMode;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class HammerUpgrade extends ToolUpgrade implements BlockBreakingUpgrade {
    public HammerUpgrade(ToolTier tier, String displayName, TagKey<Item> applicableTo) {
        super(tier, displayName, applicableTo);
    }
    
    @Override
    protected void onApply(ItemStack stack) {
        stack.set(ModDataComponents.HAMMER_MODE.get(), HammerMode.SIZE_3X3);
    }
    
    public List<HammerMode> availableHammerModes(ItemStack tool) {
        ToolTier tier = tool.get(ModDataComponents.TOOL_TIER.get());
        List<HammerMode> modes = new ArrayList<>();
        
        for (HammerMode mode : HammerMode.values()) {
            if (tier != null && tier.getId() >= mode.getMinTier().getId()) {
                modes.add(mode);
            }
        }
        
        return modes;
    }
    
    public HammerMode getMode(ItemStack tool) {
        HammerMode stored = tool.getOrDefault(ModDataComponents.HAMMER_MODE.get(), HammerMode.SIZE_3X3);
        List<HammerMode> available = availableHammerModes(tool);
        return available.contains(stored) ? stored : HammerMode.SIZE_3X3;
    }
    
    @Override
    public List<BlockPos> getBlocksToBreak(ItemStack tool, Level level, BlockPos origin, ServerPlayer player) {
        HitResult hit = player.pick(player.blockInteractionRange(), 1.0F, false);
        if (hit.getType() != HitResult.Type.BLOCK) return List.of();
        
        return getAreaPositions(tool, origin, ((BlockHitResult) hit).getDirection().getAxis());
    }

    public List<BlockPos> getAreaPositions(ItemStack tool, BlockPos origin, Direction.Axis axis) {
        int range = getMode(tool).getRange();
        List<BlockPos> positions = new ArrayList<>();
        
        for (int a = -range; a <= range; a++) {
            for (int b = -range; b <= range; b++) {
                if (a == 0 && b == 0) continue;
                
                BlockPos pos = switch (axis) {
                    case X -> origin.offset(0, b, a);
                    case Y -> origin.offset(a, 0, b);
                    case Z -> origin.offset(a, b, 0);
                };
                
                positions.add(pos);
            }
        }
        
        return positions;
    }
    
    @Override
    public void cycleUpgradeMode(ItemStack tool) {
        List<HammerMode> modes = availableHammerModes(tool);
        if (modes.size() <= 1) return;
        
        int idx = modes.indexOf(getMode(tool));
        tool.set(ModDataComponents.HAMMER_MODE.get(), modes.get((idx + 1) % modes.size()));
    }
    
    @Override
    public MiningMode getMiningMode() {
        return MiningMode.HAMMER;
    }
    
    @Override
    public float getSpeedMultiplier(ItemStack tool) {
        return getMode(tool).getSpeedMultiplier();
    }
    
    @Override
    public String getModeDescriptor(ItemStack tool) {
        return getMode(tool).getDisplayName();
    }
}
