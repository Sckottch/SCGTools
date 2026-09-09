package com.sckottch.scgtools.utility.events;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.upgrades.EnchantmentUpgrade;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

import java.util.List;

@EventBusSubscriber(modid = SCGTools.MOD_ID)
public class ModEnchantmentHandler {
    @SubscribeEvent
    public static void enchantmentLevelEvent(GetEnchantmentLevelEvent event) {
        ItemInstance instance = event.getStack();
        int bonus = 0;
        
        List<ToolUpgrade> upgrades = instance.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades == null ||  upgrades.isEmpty()) return;
        
        for (ToolUpgrade  upgrade : upgrades) {
            if (upgrade instanceof EnchantmentUpgrade) bonus++;
        }
        if (bonus == 0) return;
        
        ItemEnchantments.Mutable enchantments = event.getEnchantments();
        Holder<Enchantment> target = event.getTargetEnchant();
        
        if (target != null) {
            int current = enchantments.getLevel(target);
            if (current > 0) enchantments.set(target, current + bonus);
            return;
        }
        
        for (Holder<Enchantment> enchantment : List.copyOf(enchantments.keySet())) {
            enchantments.set(enchantment, enchantments.getLevel(enchantment) + bonus);
        }
    }
}