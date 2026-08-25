package com.sckottch.scgtools;

import com.sckottch.scgtools.item.ModItems;
import com.sckottch.scgtools.item.tools.EvolvingPicaxe;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.tools.Tool;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SCGTools.MODID);
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SCGTOOLS_ITEMS_TAB =
        CREATIVE_TABS.register("scgtools_tab_items", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.scgtools.items"))
            .icon(() -> new ItemStack(ModItems.MENDING_GEM.get()))
            .displayItems((params, out) -> {
                for (var item : ModItems.DISPLAY_ORDER) {
                    out.accept(item.get());
                }
            })
            .build()
        );
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SCGTOOLS_TOOLS_TAB =
        CREATIVE_TABS.register("scgtools_tab_tools", () -> {
                return CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.scgtools.tools"))
                    .icon(() -> new ItemStack(ModItems.PICKAXE.get()))
                    .displayItems((params, out) -> {
                        for (ToolTier tier : ToolTier.values()) {
                            ItemStack stack = new ItemStack(ModItems.PICKAXE.get());
                            EvolvingPicaxe.applyTier(stack, tier, params.holders());
                            out.accept(stack);
                        }
                    })
                    .build();
            }
        );
    
    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
