package com.sckottch.scgtools.forge;

import com.sckottch.scgtools.block.entities.ModBlockEntities;
import com.sckottch.scgtools.recipe.ForgeRecipeInput;
import com.sckottch.scgtools.recipe.ModRecipes;
import com.sckottch.scgtools.recipe.SimpleForgeRecipe;
import com.sckottch.scgtools.recipe.ToolForgeRecipe;
import com.sckottch.scgtools.utility.enums.UpgradeValidationResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeBlockEntity extends BlockEntity implements MenuProvider {
    public static final int RECIPE_SLOTS = 6;
    public static final int MAIN_SLOT = 6;
    public static final int OUTPUT_SLOT = 7;
    public static final int TOTAL_SLOTS = 8;
    
    private boolean updating = false;
    
    // o menu do cliente monta o seu próprio handler, e a capacidade precisa bater
    // com a do servidor para a previsão do shift-click não ser corrigida depois
    public static class ForgeInventory extends ItemStacksResourceHandler {
        public ForgeInventory() {
            super(TOTAL_SLOTS);
        }

        @Override
        protected int getCapacity(int index, ItemResource resource) {
            if (index == MAIN_SLOT) return 1;
            return super.getCapacity(index, resource);
        }
    }

    private final ForgeInventory inventory = new ForgeInventory() {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            ForgeBlockEntity.this.setChanged();
            if (index != OUTPUT_SLOT) ForgeBlockEntity.this.updateResult();
        }
    };
    
    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.size());
        for (int i = 0; i < inventory.size(); i++) {
            ItemAccess itemAccess = ItemAccess.forHandlerIndex(inventory, i);
            inv.setItem(i, itemAccess.getResource().toStack(itemAccess.getAmount()));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }
    
    public ForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FORGE.get(), pos, state);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.scgtools.forge");
    }
    
    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ForgeMenu(containerId, inventory, this, this.inventory);
    }
    
    private void updateResult() {
        if (updating) return;
        if (!(level instanceof ServerLevel)) return;
        
        updating = true;
        try {
            setOutputPreview(buildPreview());
        } finally {
            updating = false;
        }
    }
    
    
    private ItemStack buildPreview() {
        ForgeRecipeInput input = buildInput();
        
        var simple = getSimpleRecipe();
        if (simple.isPresent()) {
            return simple.get().value().assemble(input);
        }
        
        var toolRecipe = getToolRecipe();
        if (toolRecipe.isPresent()) {
            ItemStack result = input.main().copy();
            toolRecipe.get().value().getOperation().apply(result, level.registryAccess());
            return result;
        }
        
        return ItemStack.EMPTY;
    }
    
    private Optional<RecipeHolder<ToolForgeRecipe>> getToolRecipe() {
        if (!(level instanceof ServerLevel serverLevel)) return Optional.empty();
        return serverLevel.recipeAccess().getRecipeFor(
            ModRecipes.FORGE_TOOL_TYPE.get(), buildInput(), level
        );
    }
    
    private void setOutputPreview(ItemStack result) {
        if (result.isEmpty()){
            inventory.set(OUTPUT_SLOT, ItemResource.EMPTY, 0);
        } else {
            inventory.set(OUTPUT_SLOT, ItemResource.of(result), result.count());
        }
    }
    
    private Optional<RecipeHolder<SimpleForgeRecipe>> getSimpleRecipe() {
        if (!(level instanceof ServerLevel serverLevel)) return Optional.empty();
        
        return serverLevel.recipeAccess().getRecipeFor(
            ModRecipes.FORGE_SIMPLE_TYPE.get(), buildInput(), level
        );
    }
    
    private ForgeRecipeInput buildInput() {
        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0; i < RECIPE_SLOTS; i++) {
            ingredients.add(inventory.getResource(i).toStack(inventory.getAmountAsInt(i)));
        }
        ItemStack main = inventory.getResource(MAIN_SLOT).toStack(inventory.getAmountAsInt(MAIN_SLOT));
        return new ForgeRecipeInput(ingredients, main);
    }
    
    public record ForgeStatus(int xpCost, UpgradeValidationResult validation) {}

    public ForgeStatus getStatus() {
        var simple = getSimpleRecipe();
        if (simple.isPresent()) {
            return new ForgeStatus(simple.get().value().getXpCost(), UpgradeValidationResult.OK);
        }

        var toolRecipe = getToolRecipe();
        if (toolRecipe.isPresent()) {
            return new ForgeStatus(toolRecipe.get().value().getXpCost(), UpgradeValidationResult.OK);
        }

        return new ForgeStatus(-1, findFailure());
    }

    private UpgradeValidationResult findFailure() {
        if (!(level instanceof ServerLevel serverLevel)) return UpgradeValidationResult.OK;

        ForgeRecipeInput input = buildInput();
        if (input.main().isEmpty()) return UpgradeValidationResult.OK;

        for (RecipeHolder<ToolForgeRecipe> holder :
            serverLevel.recipeAccess().recipeMap().byType(ModRecipes.FORGE_TOOL_TYPE.get())) {

            if (!holder.value().matchesInputs(input)) continue;

            UpgradeValidationResult result = holder.value().getOperation().validate(input.main());
            if (!result.isOk()) return result;
        }

        return UpgradeValidationResult.OK;
    }

    public boolean canAfford(Player player, int cost) {
        if (cost < 0) return false;
        if (player.isCreative()) return true;
        return player.totalExperience >= totalXpForLevel(cost);
    }

    public boolean canCraft(Player player) {
        return canAfford(player, getXpCost());
    }
    
    public int getXpCost() {
        var simple = getSimpleRecipe();
        if (simple.isPresent()) return simple.get().value().getXpCost();
        
        var toolRecipe = getToolRecipe();
        return toolRecipe.map(toolForgeRecipeRecipeHolder ->
            toolForgeRecipeRecipeHolder.value().getXpCost()).orElse(-1);
    }
    
    public void onCrafted(Player player) {
        int cost = getXpCost();
        if (cost < 0) return;
        
        if (!player.isCreative()) {
            player.giveExperiencePoints(-totalXpForLevel(cost));
        }
        
        var simple = getSimpleRecipe();
        if (simple.isPresent()) {
            consumeIngredients(simple.get().value().getIngredients(), true);
        } else {
            var toolRecipe = getToolRecipe();
            toolRecipe.ifPresent(toolForgeRecipeRecipeHolder ->
                consumeIngredients(toolForgeRecipeRecipeHolder.value().getIngredients(), true)
            );
        }
        
        updateResult();
    }
    
    private int totalXpForLevel(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int)(2.5 * level * level - 40.5 * level + 360);
        return (int)(4.5 * level * level - 162.5 * level + 2220);
    }
    
    private void consumeIngredients(List<SizedIngredient> required, boolean consumeMain) {
        try (Transaction tx = Transaction.openRoot()) {
            for (SizedIngredient ingredient : required) {
                int remaining = ingredient.count();
                
                for (int i = 0; i < RECIPE_SLOTS && remaining > 0; i++) {
                    ItemResource res = inventory.getResource(i);
                    if (res.isEmpty()) continue;
                    if (!ingredient.ingredient().test(res.toStack(1))) continue;
                    
                    int taken = Math.min(inventory.getAmountAsInt(i), remaining);
                    inventory.extract(i, res, taken, tx);
                    remaining -= taken;
                }
            }
            
            if (consumeMain) {
                ItemResource main = inventory.getResource(MAIN_SLOT);
                if (!main.isEmpty()) {
                    inventory.extract(MAIN_SLOT, main, inventory.getAmountAsInt(MAIN_SLOT), tx);
                }
            }
            
            tx.commit();
        }
    }
    
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("items", inventory);
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("items").ifPresent(inventory::deserialize);
    }
}
