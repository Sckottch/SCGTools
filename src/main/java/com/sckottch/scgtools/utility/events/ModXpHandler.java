package com.sckottch.scgtools.utility.events;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.upgrades.ModUpgrades;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

import java.util.List;

@EventBusSubscriber(modid = SCGTools.MODID)
public class ModXpHandler {
    
    private static final int DURABILITY_PER_XP = 2;
    
    @SubscribeEvent
    public static void onPickupXp(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        
        List<ToolUpgrade> upgrades = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades == null || !upgrades.contains(ModUpgrades.MENDING.get())) return;
        
        int damage = stack.getDamageValue();
        if (damage <= 0) return;
        
        ExperienceOrb orb = event.getOrb();
        int xpAvailable = orb.getValue();
        
        int repaired = Math.min(xpAvailable * DURABILITY_PER_XP, damage);
        int xpUsed = (int) Math.ceil(repaired / (double) DURABILITY_PER_XP);
        
        stack.setDamageValue(damage - repaired);
        
        int leftover = xpAvailable - xpUsed;
    }
}