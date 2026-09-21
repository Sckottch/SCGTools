package com.sckottch.scgtools.datagen;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.item.ModItems;
import com.sckottch.scgtools.utility.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.references.ItemIds;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, SCGTools.MOD_ID);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.PICKAXES).add(ModItems.PICKAXE.getKey());
        tag(ItemTags.AXES).add((ModItems.AXE.getKey()));
        tag(ItemTags.SWORDS).add((ModItems.SWORD.getKey()));
        tag(ItemTags.SHOVELS).add(ModItems.SHOVEL.getKey());
        tag(ItemTags.HOES).add(ModItems.HOE.getKey());
        
        tag(ModTags.Items.FLINT_REPAIR_ITEMS).add(ItemIds.FLINT);
        tag(ModTags.Items.DRACONIC_REPAIR_ITEMS).add(ItemIds.NETHER_STAR);
        
        tag(ModTags.Items.EVOLVING_ITEM)
            .add(ModItems.PICKAXE.getKey())
            .add(ModItems.AXE.getKey())
            .add(ModItems.SWORD.getKey())
            .add(ModItems.SHOVEL.getKey())
            .add(ModItems.HOE.getKey());
        

        tag(ModTags.Items.FORGE_MAIN)
            .addTag(ModTags.Items.EVOLVING_ITEM)
            .add(ItemIds.DIAMOND);
        
        tag(ModTags.Items.MINING_TOOL)
            .add(ModItems.PICKAXE.getKey())
            .add(ModItems.AXE.getKey())
            .add(ModItems.SHOVEL.getKey())
            .add(ModItems.HOE.getKey());
        
        
        tag(ModTags.Items.WEAPON)
            .add(ModItems.SWORD.getKey());
    }
}
