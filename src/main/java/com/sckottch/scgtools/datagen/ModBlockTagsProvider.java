package com.sckottch.scgtools.datagen;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, SCGTools.MODID);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.FORGE.getKey());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.FORGE.getKey());
    }
}
