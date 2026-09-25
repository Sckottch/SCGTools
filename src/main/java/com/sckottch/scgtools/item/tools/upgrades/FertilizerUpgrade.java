package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.utility.enums.MiningMode;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FertilizerUpgrade extends ToolUpgrade implements BlockBreakingUpgrade {
    public FertilizerUpgrade(ToolTier minTier, String displayName, TagKey<Item> applicableTo) {
        super(minTier, displayName, applicableTo);
    }

    @Override
    public void onApply(ItemStack stack) {
        stack.set(ModDataComponents.FERTILIZER_RANGE.get(), getMaxRange(stack));
    }

    public int getLevel(ItemStack tool) {
        ToolTier tier = tool.get(ModDataComponents.TOOL_TIER.get());
        return tier == null ? 0 : tier.getId();
    }

    public int getMaxRange(ItemStack tool) {
        return getLevel(tool);
    }

    public int getRange(ItemStack tool) {
        int max = getMaxRange(tool);
        return Math.min(tool.getOrDefault(ModDataComponents.FERTILIZER_RANGE.get(), max), max);
    }

    @Override
    public MiningMode getMiningMode() {
        return MiningMode.SPECIAL;
    }

    @Override
    public float getSpeedMultiplier(ItemStack tool) {
        return 1.0F;
    }

    @Override
    public String getModeDescriptor(ItemStack tool) {
        int size = getRange(tool) * 2 + 1;
        return getDisplayName() + " " + size + "x" + size;
    }

    @Override
    public void cycleUpgradeMode(ItemStack tool) {
        int max = getMaxRange(tool);
        if (max <= 0) return;

        int next = (getRange(tool) + 1) % (max + 1);
        tool.set(ModDataComponents.FERTILIZER_RANGE.get(), next);
    }

    @Override
    public List<BlockPos> getBlocksToBreak(ItemStack tool, Level level, BlockPos origin, ServerPlayer player) {
        return List.of();
    }

    @Override
    public List<BlockPos> getBlocksToUse(ItemStack tool, Level level, BlockPos origin, Direction face) {
        int range = getRange(tool);
        List<BlockPos> positions = new ArrayList<>();

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                if (x == 0 && z == 0) continue;
                positions.add(origin.offset(x, 0, z));
            }
        }

        return positions;
    }

    public static @Nullable FertilizerUpgrade active(ItemStack stack) {
        if (stack.getOrDefault(ModDataComponents.MINING_MODE.get(), MiningMode.NORMAL) != MiningMode.SPECIAL) return null;

        List<ToolUpgrade> upgrades = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades == null) return null;

        for (ToolUpgrade upgrade : upgrades) {
            if (upgrade instanceof FertilizerUpgrade fertilizer) return fertilizer;
        }
        return null;
    }
}
