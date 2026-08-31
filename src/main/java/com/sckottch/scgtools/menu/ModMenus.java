package com.sckottch.scgtools.menu;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.block.entities.forge.ForgeMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(
        Registries.MENU, SCGTools.MODID
    );
    
    public static final Supplier<MenuType<ForgeMenu>> FORGE =
        MENUS.register("forge", () -> IMenuTypeExtension.create(ForgeMenu::new));
    
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
