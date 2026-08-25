package com.sckottch.scgtools.datagen;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.properties.select.CustomModelDataProperty;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, SCGTools.MODID);
    }
    
    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.MENDING_GEM.get(), ModelTemplates.FLAT_ITEM);
        
        setPickaxeModels(itemModels);
    }
    
    private void setPickaxeModels(ItemModelGenerators itemModels) {
        Identifier flintModel = itemModels.createFlatItemModel(
            ModItems.PICKAXE.get(), "_flint", ModelTemplates.FLAT_HANDHELD_ITEM
        );
        Identifier ironModel =  itemModels.createFlatItemModel(
            ModItems.PICKAXE.get(), "_iron", ModelTemplates.FLAT_HANDHELD_ITEM
        );
        
        itemModels.itemModelOutput.accept(
            ModItems.PICKAXE.get(),
            ItemModelUtils.select(
                new CustomModelDataProperty(0),
                ItemModelUtils.plainModel(flintModel),
                ItemModelUtils.when("flint", ItemModelUtils.plainModel(flintModel)),
                ItemModelUtils.when("iron", ItemModelUtils.plainModel(ironModel))
            )
        );
    }
}
