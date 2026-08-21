package com.sckottch.scgtools;

import com.sckottch.scgtools.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SCGTools.MODID);
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SCGTOOLS_TAB =
        CREATIVE_TABS.register("scgtools_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.scgtools"))
            .icon(() -> new ItemStack(ModItems.MENDING_GEM.get()))
            .displayItems((params, out) -> {
                for (var item : ModItems.DISPLAY_ORDER) {
                    out.accept(item.get());
                }
            })
            .build()
        );
    
    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
