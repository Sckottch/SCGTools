package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.utility.enums.MiningMode;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.search.BlockSearch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ChainsawUpgrade extends ToolUpgrade implements BlockBreakingUpgrade {
    public static final int MAX_BLOCKS = 256;
    
    public ChainsawUpgrade(ToolTier minTier, String displayName, TagKey<Item> applicableTo) {
        super(minTier, displayName, applicableTo);
    }
    
    @Override
    public MiningMode getMiningMode() {
        return MiningMode.SPECIAL;
    }
    
    @Override
    public String getModeDescriptor(ItemStack tool) {
        return getDisplayName();
    }
    
    @Override
    public float getSpeedMultiplier(ItemStack tool) {
        return 1.0F;
    }
    
    @Override
    public List<BlockPos> getBlocksToBreak(ItemStack tool, Level level, BlockPos origin, ServerPlayer player) {
        BlockState originState = level.getBlockState(origin);
        if (!originState.is(BlockTags.LOGS)) return List.of();
        
        Block log = originState.getBlock();
        List<BlockPos> logs = BlockSearch.floodFill(level, origin, state -> state.is(log), MAX_BLOCKS);
        boolean isTree = hasNaturalLeaves(level, origin) || logs.stream().anyMatch(pos -> hasNaturalLeaves(level, pos));
        if (!isTree) return List.of();
        
        return logs;
    }
    
    private static boolean hasNaturalLeaves(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockState state = level.getBlockState(pos.relative(direction));
            if (state.is(BlockTags.LEAVES) && !state.getValueOrElse(LeavesBlock.PERSISTENT, true)) {
                return true;
            }
        }
        return false;
    }
}
