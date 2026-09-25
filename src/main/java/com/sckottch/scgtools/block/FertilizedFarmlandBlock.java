package com.sckottch.scgtools.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.common.FarmlandWaterManager;

public class FertilizedFarmlandBlock extends FarmlandBlock {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 4);
    public static final BooleanProperty FRESH = BooleanProperty.create("fresh");

    private static final int GRACE_TICKS = 500;

    public FertilizedFarmlandBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MOISTURE, 0).setValue(LEVEL, 1).setValue(FRESH, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LEVEL, FRESH);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (state.getValue(FRESH)) {
            level.scheduleTick(pos, this, GRACE_TICKS);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        BlockState current = level.getBlockState(pos);
        if (current.is(this) && current.getValue(FRESH)) {
            level.setBlock(pos, current.setValue(FRESH, false), 2);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(FRESH)) {
            super.randomTick(state, level, pos, random);
        } else if (state.getValue(MOISTURE) < MAX_MOISTURE && (isNearWater(level, pos) || level.isRainingAt(pos.above()))) {
            level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
        }

        BlockState current = level.getBlockState(pos);
        if (!current.is(this) || current.getValue(MOISTURE) == 0) return;

        BlockPos above = pos.above();
        int extraTicks = current.getValue(LEVEL);

        for (int i = 0; i < extraTicks; i++) {
            BlockState plant = level.getBlockState(above);
            if (!plant.isRandomlyTicking()) break;
            plant.randomTick(level, above, random);
        }
    }

    // cópia do isNearWater do FarmlandBlock, que é privado: permite hidratar durante a janela sem passar pelo ramo de secar
    private static boolean isNearWater(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (state.canBeHydrated(level, pos, level.getFluidState(blockPos), blockPos)) {
                return true;
            }
        }

        return FarmlandWaterManager.hasBlockWaterTicket(level, pos);
    }
}
