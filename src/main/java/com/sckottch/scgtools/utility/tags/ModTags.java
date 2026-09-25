package com.sckottch.scgtools.utility.tags;

import com.sckottch.scgtools.SCGTools;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> EVOLVING_ITEM = createTag("evolving_tool");
        public static final TagKey<Item> FORGE_MAIN = createTag("forge_main");
        public static final TagKey<Item> MINING_TOOL = createTag("mining_tool");
        public static final TagKey<Item> WEAPON = createTag("weapon");
        public static final TagKey<Item> FLINT_REPAIR_ITEMS  = createTag("flint_repair_items");
        public static final TagKey<Item> DRACONIC_REPAIR_ITEMS = createTag("draconic_repair_items");
        
        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath(SCGTools.MOD_ID, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> COMPACTABLE = createTag("compactable");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath(SCGTools.MOD_ID, name));
        }
    }
}
