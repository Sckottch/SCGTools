package com.sckottch.scgtools.forge.operation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.EvolvingTool;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.enums.UpgradeValidationResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

public record TierUpOperation(ToolTier from, ToolTier to) implements ForgeOperation {
    public static final MapCodec<TierUpOperation> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ToolTier.CODEC.fieldOf("from").forGetter(TierUpOperation::from),
            ToolTier.CODEC.fieldOf("to").forGetter(TierUpOperation::to)
        ).apply(inst, TierUpOperation::new)
    );
    
    @Override
    public MapCodec<? extends ForgeOperation> codec() {
        return CODEC;
    }
    
    @Override
    public String descriptor() {
        return to.getSerializedName();
    }

    @Override
    public UpgradeValidationResult validate(ItemStack tool) {
        if (!(tool.getItem() instanceof EvolvingTool)) return UpgradeValidationResult.INVALID;

        ToolTier tier = tool.get(ModDataComponents.TOOL_TIER.get());
        if (tier == null) return UpgradeValidationResult.INVALID;
        if (tier == from) return UpgradeValidationResult.OK;

        return tier.getId() < from.getId()
            ? UpgradeValidationResult.WRONG_TIER
            : UpgradeValidationResult.NOT_APPLICABLE;
    }
    
    @Override
    public void apply(ItemStack tool, HolderLookup.Provider lookup) {
        if (tool.getItem() instanceof EvolvingTool evoTool) {
            evoTool.applyTier(tool, to, lookup);
        }
    }
}
