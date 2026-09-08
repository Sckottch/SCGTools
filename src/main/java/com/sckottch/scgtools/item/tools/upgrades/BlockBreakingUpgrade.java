package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.utility.enums.MiningMode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public interface BlockBreakingUpgrade {
    MiningMode getMiningMode();
    float getSpeedMultiplier(ItemStack tool);
    List<BlockPos> getBlocksToBreak(ItemStack tool, Level level, BlockPos origin, ServerPlayer player);
}
