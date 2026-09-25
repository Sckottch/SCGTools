package com.sckottch.scgtools.utility.client;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.block.CompactedEarthBlock;
import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.item.tools.upgrades.CompactorUpgrade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = SCGTools.MOD_ID, value = Dist.CLIENT)
public class CompactedEarthParticles {
    private static final int RADIUS = 8;
    private static final int INTERVAL = 10;
    private static final int[] COLORS = { 0, 0xD0D0D0, 0x4AEDD9, 0xFFAA00, 0xB84DFF };

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.isPaused() || player.tickCount % INTERVAL != 0) return;
        if (CompactorUpgrade.active(player.getMainHandItem()) == null) return;

        Level level = player.level();
        BlockPos center = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RADIUS, -3, -RADIUS), center.offset(RADIUS, 1, RADIUS))) {
            BlockState state = level.getBlockState(pos);
            if (!state.is(ModBlocks.COMPACTED_EARTH.get())) continue;

            double y = surfaceY(level, pos);
            if (Double.isNaN(y)) continue;

            int color = COLORS[state.getValue(CompactedEarthBlock.LEVEL)];
            level.addParticle(new DustParticleOptions(color, 1.0F), pos.getX() + 0.5, y, pos.getZ() + 0.5, 0, 0, 0);
        }
    }

    private static double surfaceY(Level level, BlockPos pos) {
        BlockPos above = pos.above();
        VoxelShape shape = level.getBlockState(above).getCollisionShape(level, above);
        double top = shape.isEmpty() ? 0.0 : shape.max(Direction.Axis.Y);

        if (top >= 1.0 && !level.getBlockState(above.above()).getCollisionShape(level, above.above()).isEmpty()) {
            return Double.NaN;
        }

        return above.getY() + top + 0.05;
    }
}
