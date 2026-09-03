package com.sckottch.scgtools.utility.tags;

import com.sckottch.scgtools.SCGTools;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> EVOLVING_TOOL = createTag("evolving_tool");
        public static final TagKey<Item> FORGE_MAIN = createTag("forge_main");
        public static final TagKey<Item> MINING_TOOL = createTag("mining_tool");
        public static final TagKey<Item> WEAPON = createTag("weapon");
        
        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath(SCGTools.MOD_ID, name));
        }
    }
}
