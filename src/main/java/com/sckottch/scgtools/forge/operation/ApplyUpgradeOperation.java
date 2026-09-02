package com.sckottch.scgtools.forge.operation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.components.ToolLevelData;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.registries.ModRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ApplyUpgradeOperation(ToolUpgrade upgrade) implements ForgeOperation {
    public static final MapCodec<ApplyUpgradeOperation> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        ModRegistries.UPGRADE_REGISTRY.byNameCodec().fieldOf("upgrade")
            .forGetter(ApplyUpgradeOperation::upgrade)
    ).apply(inst, ApplyUpgradeOperation::new));
    
    @Override
    public MapCodec<? extends ForgeOperation> codec() { return CODEC; }

    @Override
    public String descriptor() {
        return ModRegistries.UPGRADE_REGISTRY.getKey(upgrade).getPath();
    }

    @Override
    public boolean canApply(ItemStack tool) {
        ToolTier tier = tool.get(ModDataComponents.TOOL_TIER.get());
        ToolLevelData level = tool.get(ModDataComponents.TOOL_LEVEL.get());
        List<ToolUpgrade> current = tool.get(ModDataComponents.TOOL_UPGRADES.get());
        if (tier == null || level == null || current == null) return false;
        
        return upgrade.canApplyTo(tier)
            && current.size() < level.slotCount()
            && !current.contains(upgrade);
    }
    
    @Override
    public void apply(ItemStack tool, HolderLookup.Provider lookup) {
        List<ToolUpgrade> current = tool.get(ModDataComponents.TOOL_UPGRADES.get());
        List<ToolUpgrade> updated = new ArrayList<>(current);
        updated.add(upgrade);
        tool.set(ModDataComponents.TOOL_UPGRADES.get(), List.copyOf(updated));
        upgrade.onApply(tool);
    }
}
