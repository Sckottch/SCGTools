package com.sckottch.scgtools.forge.operation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.utility.enums.UpgradeValidationResult;
import com.sckottch.scgtools.utility.registries.ModRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

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
    public UpgradeValidationResult validate(ItemStack tool) {
        return upgrade.validate(tool);
    }

    @Override
    public void apply(ItemStack tool, HolderLookup.Provider lookup) {
        upgrade.applyTo(tool);
    }
}
