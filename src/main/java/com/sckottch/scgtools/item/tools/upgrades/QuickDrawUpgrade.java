package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class QuickDrawUpgrade extends ToolUpgrade implements AttributeUpgrade {
    private static final Identifier MODIFIER_ID = Identifier.fromNamespaceAndPath(SCGTools.MOD_ID, "quick_draw");
    private static final double BONUS_PER_TIER = 0.05;
    
    public QuickDrawUpgrade(ToolTier minTier, String displayName, TagKey<Item> applicableTo) {
        super(minTier, displayName, applicableTo);
    }
    
    @Override
    public void applyAttributes(ItemStack tool, ItemAttributeModifierEvent event) {
        ToolTier tier = tool.get(ModDataComponents.TOOL_TIER.get());
        if (tier == null) return;
        
        double bonus = BONUS_PER_TIER * (tier.getId() + 1);
        
        event.addModifier(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            EquipmentSlotGroup.MAINHAND
        );
    }
}
