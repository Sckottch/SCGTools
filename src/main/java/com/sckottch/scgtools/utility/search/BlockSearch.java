package com.sckottch.scgtools.utility.search;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.Predicate;

public final class BlockSearch {
    private BlockSearch() {}
    
    public static List<BlockPos> floodFill(Level level, BlockPos origin, Predicate<BlockState> matches, int maxBlocks) {
        Set<BlockPos> visited = new HashSet<>();
        visited.add(origin);
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(origin);
        List<BlockPos> result = new ArrayList<>();
        
        while (!queue.isEmpty() && result.size() < maxBlocks) {
            BlockPos current = queue.poll();
            
            for (BlockPos neighbor : BlockPos.betweenClosed(current.offset(-1,-1,-1), current.offset(1,1,1))) {
                if (result.size() >= maxBlocks) break;
                
                BlockPos pos = neighbor.immutable();
                if (!visited.add(pos)) continue;
                if (!matches.test(level.getBlockState(pos))) continue;
                
                result.add(pos);
                queue.add(pos);
            }
        }
        
        return result;
    }
}
