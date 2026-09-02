package com.sckottch.scgtools.block;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.forge.ForgeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SCGTools.MODID);
    
    public static final DeferredBlock<ForgeBlock> FORGE = BLOCKS.registerBlock(
        "forge",
        ForgeBlock::new,
        () -> BlockBehaviour.Properties.of()
            .strength(3.5f)
            .requiresCorrectToolForDrops()
    );
    
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
