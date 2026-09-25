package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.utility.enums.MiningMode;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.search.BlockSearch;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class VeinMineUpgrade extends ToolUpgrade implements BlockBreakingUpgrade{
    private static final int MAX_BOCKS = 128;
    
    public VeinMineUpgrade(ToolTier minTier, String displayName, TagKey<Item> applicableTo) {
        super(minTier, displayName, applicableTo);
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
        return getDisplayName();
    }
    
    @Override
    public List<BlockPos> getBlocksToBreak(ItemStack tool, Level level, BlockPos origin, ServerPlayer player) {
        BlockState originState = level.getBlockState(origin);
        if (!originState.is(Tags.Blocks.ORES)) return List.of();
        
        Predicate<BlockState> sameOre = oreMatcher(originState);
        
        return BlockSearch.floodFill(level, origin, sameOre, MAX_BOCKS);
    }
    
    private Predicate<BlockState> oreMatcher(BlockState origin) {
        Optional<TagKey<Block>> oreTag = origin.tags()
            .filter(tag -> tag.location().getNamespace().equals("c") &&
                tag.location().getPath().startsWith("ores/"))
            .findFirst();
        
        if (oreTag.isPresent()) {
            TagKey<Block> tag = oreTag.get();
            return state -> state.is(tag);
        }
        
        Block block = origin.getBlock();
        return state -> state.is(block);
    }
}
