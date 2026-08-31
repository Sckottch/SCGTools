package com.sckottch.scgtools.datagen;

import com.sckottch.scgtools.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {
     public ModBlockLootProvider(HolderLookup.Provider lookupProvider) {
         super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
     }
    
    @Override
    protected void generate() {
        dropSelf(ModBlocks.FORGE.get());
    }
    
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
            .map(holder -> (Block)holder.value())
            .toList();
    }
}
