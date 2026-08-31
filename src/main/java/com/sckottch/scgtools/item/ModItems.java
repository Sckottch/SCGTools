package com.sckottch.scgtools.item;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.components.ToolLevelData;
import com.sckottch.scgtools.item.tools.EvolvingAxe;
import com.sckottch.scgtools.item.tools.EvolvingPicaxe;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SCGTools.MODID);
    
    public static final DeferredItem<Item> MENDING_GEM = ITEMS.registerSimpleItem("mending_gem", props ->
        props.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );
    
    public static final DeferredItem<BlockItem> FORGE_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.FORGE);
    
    public static final DeferredItem<EvolvingPicaxe> PICKAXE = ITEMS.registerItem(
        "pickaxe",
        props -> new EvolvingPicaxe(
            props.pickaxe(ModToolMaterials.FLINT_MATERIAL, 1, -2.8f)
                .component(ModDataComponents.TOOL_LEVEL.get(), ToolLevelData.INITIAL)
                .component(ModDataComponents.TOOL_TIER.get(), ToolTier.FLINT)
                .component(ModDataComponents.TOOL_UPGRADES.get(), List.of())
        )
    );
    
    public static final DeferredItem<EvolvingAxe> AXE = ITEMS.registerItem(
        "axe",
        props -> new EvolvingAxe(ModToolMaterials.FLINT_MATERIAL, 7.0F, -3.2F, props
            .component(ModDataComponents.TOOL_LEVEL.get(), ToolLevelData.INITIAL)
            .component(ModDataComponents.TOOL_TIER.get(), ToolTier.FLINT)
            .component(ModDataComponents.TOOL_UPGRADES.get(), List.of())
        )
    );
    
    public static final List<DeferredItem<? extends Item>> DISPLAY_ORDER = List.of(
        FORGE_ITEM,
        MENDING_GEM
    );
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
