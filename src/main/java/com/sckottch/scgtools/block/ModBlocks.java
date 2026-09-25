package com.sckottch.scgtools.block;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.forge.ForgeBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SCGTools.MOD_ID);
    
    public static final DeferredBlock<ForgeBlock> FORGE = BLOCKS.registerBlock(
        "forge",
        ForgeBlock::new,
        () -> BlockBehaviour.Properties.of()
            .strength(3.5f)
            .requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<FertilizedFarmlandBlock> FERTILIZED_FARMLAND = BLOCKS.registerBlock(
        "fertilized_farmland",
        FertilizedFarmlandBlock::new,
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND)
    );

    public static final DeferredBlock<CompactedEarthBlock> COMPACTED_EARTH = BLOCKS.registerBlock(
        "compacted_earth",
        CompactedEarthBlock::new,
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.COARSE_DIRT)
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
