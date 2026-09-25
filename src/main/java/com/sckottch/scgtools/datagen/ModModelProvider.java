package com.sckottch.scgtools.datagen;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.properties.select.CustomModelDataProperty;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, SCGTools.MOD_ID);
    }
    
    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.MENDING_GEM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.DRAGON_TOOTH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.DRACONIC_TEMPLATE.get(), ModelTemplates.FLAT_ITEM);
        
        blockModels.createTrivialBlock(ModBlocks.FORGE.get(), TexturedModel.CUBE_TOP_BOTTOM);

        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(ModBlocks.FERTILIZED_FARMLAND.get()).with(
                BlockModelGenerators.createEmptyOrFullDispatch(
                    FarmlandBlock.MOISTURE, 7,
                    BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.FARMLAND, "_moist")),
                    BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.FARMLAND))
                )
            )
        );

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
            ModBlocks.COMPACTED_EARTH.get(),
            BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.COARSE_DIRT))
        ));

        setEvolvingModels(ModItems.PICKAXE.get(), itemModels);
        setEvolvingModels(ModItems.AXE.get(), itemModels);
        setEvolvingModels(ModItems.SWORD.get(), itemModels);
        setEvolvingModels(ModItems.SHOVEL.get(), itemModels);
        setEvolvingModels(ModItems.HOE.get(), itemModels);
    }
    
    private void setEvolvingModels(Item evolvingItem, ItemModelGenerators itemModels) {
        Identifier flintModel = itemModels.createFlatItemModel(
            evolvingItem, "_flint", ModelTemplates.FLAT_HANDHELD_ITEM
        );
        Identifier ironModel =  itemModels.createFlatItemModel(
            evolvingItem, "_iron", ModelTemplates.FLAT_HANDHELD_ITEM
        );
        Identifier diamondModel =  itemModels.createFlatItemModel(
            evolvingItem, "_diamond", ModelTemplates.FLAT_HANDHELD_ITEM
        );
        Identifier netheriteModel =  itemModels.createFlatItemModel(
            evolvingItem, "_netherite", ModelTemplates.FLAT_HANDHELD_ITEM
        );
        Identifier draconicModel =  itemModels.createFlatItemModel(
            evolvingItem, "_draconic", ModelTemplates.FLAT_HANDHELD_ITEM
        );
        
        itemModels.itemModelOutput.accept(
            evolvingItem,
            ItemModelUtils.select(
                new CustomModelDataProperty(0),
                ItemModelUtils.plainModel(flintModel),
                ItemModelUtils.when("flint", ItemModelUtils.plainModel(flintModel)),
                ItemModelUtils.when("iron", ItemModelUtils.plainModel(ironModel)),
                ItemModelUtils.when("diamond", ItemModelUtils.plainModel(diamondModel)),
                ItemModelUtils.when("netherite", ItemModelUtils.plainModel(netheriteModel)),
                ItemModelUtils.when("draconic", ItemModelUtils.plainModel(draconicModel))
            )
        );
    }
}
