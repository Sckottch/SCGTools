package com.sckottch.scgtools.utility.events;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.upgrades.AttributeUpgrade;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.List;

@EventBusSubscriber(modid = SCGTools.MOD_ID)
public class ModAttributeEvents {
    @SubscribeEvent
    public static void onItemAttribute(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        
        List<ToolUpgrade> upgrades = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades == null || upgrades.isEmpty()) return;
        
        for (ToolUpgrade upgrade : upgrades) {
            if (upgrade instanceof AttributeUpgrade attributeUpgrade) {
                attributeUpgrade.applyAttributes(stack, event);
            }
        }
    }
}
