package com.sckottch.scgtools.datagen;

import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.datagen.custom.SimpleForgeRecipeBuilder;
import com.sckottch.scgtools.datagen.custom.ToolForgeRecipeBuilder;
import com.sckottch.scgtools.forge.operation.ApplyUpgradeOperation;
import com.sckottch.scgtools.forge.operation.TierUpOperation;
import com.sckottch.scgtools.item.ModItems;
import com.sckottch.scgtools.item.tools.upgrades.ModUpgrades;
import com.sckottch.scgtools.recipe.ToolRepairRecipe;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }
    
    @Override
    protected void buildRecipes() {
        //Recipes
        shaped(RecipeCategory.DECORATIONS, ModBlocks.FORGE)
            .pattern("III")
            .pattern("DCD")
            .pattern("DDD")
            .define('I', Items.IRON_INGOT)
            .define('D', Items.COBBLED_DEEPSLATE)
            .define('C', Items.CRAFTING_TABLE)
            .unlockedBy("has_iron", this.has(Items.IRON_INGOT))
            .save(this.output);
        
        shapeless(RecipeCategory.MISC, Items.FLINT)
            .requires(Items.GRAVEL, 3)
            .unlockedBy("has_gravel", this.has(Items.GRAVEL))
            .save(this.output, "scgtools:flint");
        
        SpecialRecipeBuilder.special(ToolRepairRecipe::new).save(this.output, "scgtools:tool_repair");
        
        //Tools
        shaped(RecipeCategory.TOOLS, ModItems.PICKAXE)
            .pattern("FFF")
            .pattern(" S ")
            .pattern(" S ")
            .define('F', Items.FLINT)
            .define('S', Items.STICK)
            .unlockedBy("has_flint", this.has(Items.FLINT))
            .save(this.output);
        
        shaped(RecipeCategory.TOOLS, ModItems.AXE)
            .pattern("FF")
            .pattern("FS")
            .pattern(" S")
            .define('F', Items.FLINT)
            .define('S', Items.STICK)
            .unlockedBy("has_flint", this.has(Items.FLINT))
            .save(this.output);
        
        shaped(RecipeCategory.TOOLS, ModItems.SWORD)
            .pattern("F")
            .pattern("F")
            .pattern("S")
            .define('F', Items.FLINT)
            .define('S', Items.STICK)
            .unlockedBy("has_flint", this.has(Items.FLINT))
            .save(this.output);
        
        shaped(RecipeCategory.TOOLS, ModItems.SHOVEL)
            .pattern("F")
            .pattern("S")
            .pattern("S")
            .define('F', Items.FLINT)
            .define('S', Items.STICK)
            .unlockedBy("has_flint", this.has(Items.FLINT))
            .save(this.output);
        
        shaped(RecipeCategory.TOOLS, ModItems.HOE)
            .pattern("FF")
            .pattern(" S")
            .pattern(" S")
            .define('F', Items.FLINT)
            .define('S', Items.STICK)
            .unlockedBy("has_flint", this.has(Items.FLINT))
            .save(this.output);
        
        // Forge Recipes
        Ingredient mendingBook = getEnchantedBook(Enchantments.MENDING, 1);
        new SimpleForgeRecipeBuilder(
            new ItemStackTemplate(ModItems.MENDING_GEM.get()),
            Ingredient.of(Items.DIAMOND)
        )
            .ingredient(new SizedIngredient(mendingBook, 1))
            .xpCost(30)
            .save(this.output);
        
        // Tiers
        new ToolForgeRecipeBuilder(
            Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModTags.Items.EVOLVING_ITEM)),
            new TierUpOperation(ToolTier.FLINT, ToolTier.IRON)
        )
            .ingredient(Items.IRON_INGOT, 3)
            .save(this.output);
        
        new ToolForgeRecipeBuilder(
            Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModTags.Items.EVOLVING_ITEM)),
            new TierUpOperation(ToolTier.IRON, ToolTier.DIAMOND)
        )
            .ingredient(Items.DIAMOND, 2)
            .ingredient(Items.LAPIS_LAZULI, 2)
            .ingredient(Items.REDSTONE,  1)
            .save(this.output);
        
        new ToolForgeRecipeBuilder(
            Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModTags.Items.EVOLVING_ITEM)),
            new TierUpOperation(ToolTier.DIAMOND, ToolTier.NETHERITE)
        )
            .ingredient(Items.NETHERITE_INGOT, 1)
            .ingredient(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1)
            .ingredient(Items.GOLD_INGOT, 1)
            .save(this.output);
        
        new ToolForgeRecipeBuilder(
            Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModTags.Items.EVOLVING_ITEM)),
            new TierUpOperation(ToolTier.NETHERITE, ToolTier.DRACONIC)
        )
            .ingredient(Items.NETHER_STAR, 1)
            .ingredient(ModItems.DRAGON_TOOTH, 2)
            .ingredient(ModItems.DRACONIC_TEMPLATE, 1)
            .ingredient(Items.NETHERITE_INGOT, 1)
            .save(this.output);
        
        //Upgrades
        new ToolForgeRecipeBuilder(
            Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModTags.Items.EVOLVING_ITEM)),
            new ApplyUpgradeOperation(ModUpgrades.MENDING.get())
        )
            .ingredient(ModItems.MENDING_GEM.get(), 1)
            .ingredient(Items.EMERALD, 5)
            .ingredient(Items.EMERALD_BLOCK, 5)
            .xpCost(30)
            .save(this.output);
        
        new ToolForgeRecipeBuilder(
            Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModTags.Items.MINING_TOOL)),
            new ApplyUpgradeOperation(ModUpgrades.HAMMER.get())
        )
            .ingredient(Items.IRON_BLOCK, 3)
            .ingredient(Items.COBBLESTONE, 32)
            .ingredient(Items.DIAMOND, 2)
            .ingredient(Items.IRON_PICKAXE, 2)
            .save(this.output);
        
        Ingredient efficiencyVBook = getEnchantedBook(Enchantments.EFFICIENCY, 5);
        Ingredient sharpnessVBook = getEnchantedBook(Enchantments.SHARPNESS, 5);
        new ToolForgeRecipeBuilder(
            Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModTags.Items.EVOLVING_ITEM)),
            new ApplyUpgradeOperation(ModUpgrades.ENCHANTMENT_I.get())
        )
            .ingredient(Items.DIAMOND_BLOCK, 2)
            .ingredient(Items.GOLD_BLOCK, 2)
            .ingredient(new SizedIngredient(efficiencyVBook, 1))
            .ingredient(new SizedIngredient(sharpnessVBook, 1))
            .xpCost(45)
            .save(this.output);
        
        new ToolForgeRecipeBuilder(
            Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModTags.Items.EVOLVING_ITEM)),
            new ApplyUpgradeOperation(ModUpgrades.ENCHANTMENT_II.get())
        )
            .ingredient(Items.NETHER_STAR, 1)
            .ingredient(ModItems.DRAGON_TOOTH, 2)
            .ingredient(Items.DIAMOND_BLOCK, 3)
            .ingredient(Items.GOLD_BLOCK, 5)
            .ingredient(Items.EMERALD_BLOCK, 10)
            .ingredient(Items.DRAGON_BREATH, 8)
            .xpCost(60)
            .save(this.output);
    }
    
    private Ingredient getEnchantedBook(ResourceKey<Enchantment> enchantment, int level) {
        ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchants.set(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment), level);
        
        return DataComponentIngredient.of(
            false, DataComponents.STORED_ENCHANTMENTS, enchants.toImmutable(), Items.ENCHANTED_BOOK
        ).getCustomIngredient().toVanilla();
    }
    
    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
            super(output, lookup);
        }
        
        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new ModRecipeProvider(provider, recipeOutput);
        }
        
        @Override
        public String getName() {
            return "SCG Tools Recipes";
        }
    }
}
