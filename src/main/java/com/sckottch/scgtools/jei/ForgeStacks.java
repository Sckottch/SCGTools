package com.sckottch.scgtools.jei;

import com.sckottch.scgtools.forge.operation.ForgeOperation;
import com.sckottch.scgtools.item.tools.EvolvingTool;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ForgeStacks {
    private ForgeStacks() {}

    public static Optional<HolderLookup.Provider> lookup() {
        Level level = Minecraft.getInstance().level;
        return level == null ? Optional.empty() : Optional.of(level.registryAccess());
    }

    public static List<Item> toolsIn(TagKey<Item> tag) {
        List<Item> items = new ArrayList<>();

        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            if (holder.value() instanceof EvolvingTool) items.add(holder.value());
        }

        return items;
    }

    public static ItemStack toolAtTier(Item item, ToolTier tier) {
        ItemStack stack = item.getDefaultInstance();

        if (item instanceof EvolvingTool tool) {
            lookup().ifPresent(provider -> tool.applyTier(stack, tier, provider));
        }

        return stack;
    }

    public static List<ItemStack> toolsFromTier(TagKey<Item> tag, ToolTier minTier) {
        List<ItemStack> stacks = new ArrayList<>();

        for (Item item : toolsIn(tag)) {
            for (ToolTier tier : ToolTier.values()) {
                if (tier.getId() >= minTier.getId()) stacks.add(toolAtTier(item, tier));
            }
        }

        return stacks;
    }

    public static ItemStack applyOperation(ItemStack input, ForgeOperation operation) {
        ItemStack result = input.copy();
        lookup().ifPresent(provider -> operation.apply(result, provider));

        return result;
    }

    public static List<ItemStack> applyOperation(List<ItemStack> inputs, ForgeOperation operation) {
        return inputs.stream().map(input -> applyOperation(input, operation)).toList();
    }

    public static List<ItemStack> materialStacks(SizedIngredient sized, ContextMap context) {
        return sized.ingredient().display().resolveForStacks(context).stream()
            .map(stack -> stack.copyWithCount(sized.count()))
            .toList();
    }
}
