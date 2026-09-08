package com.sckottch.scgtools.item.tools.upgrades;

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
    private static final int RANGE = 1;
    
    public HammerUpgrade(ToolTier tier, String displayName, TagKey<Item> applicableTo) {
        super(tier, displayName, applicableTo);
    }
    
    @Override
    public List<BlockPos> getBlocksToBreak(ItemStack tool, Level level, BlockPos origin, ServerPlayer player) {
        HitResult hit = player.pick(player.blockInteractionRange(), 1.0F, false);
        if (hit.getType() != HitResult.Type.BLOCK) return List.of();
        
        Direction.Axis axis = ((BlockHitResult) hit).getDirection().getAxis();
        List<BlockPos> positions = new ArrayList<>();
        
        for (int a = -RANGE; a <= RANGE; a++) {
            for (int b = -RANGE; b <= RANGE; b++) {
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
    public MiningMode getMiningMode() {
        return MiningMode.HAMMER;
    }
    
    @Override
    public float getSpeedMultiplier(ItemStack tool) {
        return 0.75F;
    }
}
