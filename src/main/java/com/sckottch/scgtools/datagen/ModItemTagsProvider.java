package com.sckottch.scgtools.datagen;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.item.ModItems;
import com.sckottch.scgtools.item.ModToolMaterials;
import com.sckottch.scgtools.utility.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.references.ItemIds;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, SCGTools.MODID);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.PICKAXES).add(ModItems.PICKAXE.getKey());
        tag(ModToolMaterials.FLINT_REPAIR_ITEMS).add(ItemIds.FLINT);
        
        tag(ModTags.Items.EVOLVING_TOOL)
            .add(ModItems.PICKAXE.getKey())
            .add(ModItems.AXE.getKey());
    }
}
