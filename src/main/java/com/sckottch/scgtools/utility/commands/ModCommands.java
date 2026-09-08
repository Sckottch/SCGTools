package com.sckottch.scgtools.utility.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.EvolvingTool;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.enums.UpgradeValidationResult;
import com.sckottch.scgtools.utility.registries.ModRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = SCGTools.MOD_ID)
public class ModCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> tierNode = Commands.literal("tier");
        LiteralArgumentBuilder<CommandSourceStack> upgradeNode = Commands.literal("upgrade");
        
        for (ToolTier tier : ToolTier.values()) {
            tierNode.then(
                Commands.literal(tier.getSerializedName())
                    .executes(ctx -> setTier(ctx, tier))
            );
        }
        
        for (ToolUpgrade upgrade : ModRegistries.UPGRADE_REGISTRY) {
            Identifier id = ModRegistries.UPGRADE_REGISTRY.getKey(upgrade);
            upgradeNode.then(
                Commands.literal(id.getPath())
                    .executes(ctx -> addUpgrade(ctx, upgrade))
            );
        }
        
        
        event.getDispatcher().register(
            Commands.literal("scgtools")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(tierNode)
                .then(Commands.literal("refresh").executes(ModCommands::refresh))
                .then(upgradeNode)
        );
    }
    
    private static int setTier(CommandContext<CommandSourceStack> ctx, ToolTier tier)
    throws CommandSyntaxException
    {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        
        if (!(stack.getItem() instanceof EvolvingTool tool)) {
            ctx.getSource().sendFailure(
                Component.literal("Hold a SCG Tool in your main hand to perform this command")
            );
            return 0;
        }
        
        tool.applyTier(stack, tier, player.level().registryAccess());
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Changed weapon tier to: " + tier.getDisplayName()), true
        );
        return 1;
    }
    
    private static int refresh(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        
        if (!(stack.getItem() instanceof EvolvingTool tool)) {
            ctx.getSource().sendFailure(
                Component.literal("Hold a SCG Tool in your main hand to perform this command")
            );
            return 0;
        }
        
        ToolTier tier  = stack.get(ModDataComponents.TOOL_TIER.get());
        if (tier == null) {
            ctx.getSource().sendFailure(Component.literal("No valid Tier for tool"));
            return 0;
        }
        
        tool.applyTier(stack, tier, player.level().registryAccess());
        
        ctx.getSource().sendSuccess(() -> Component.literal("Tool Updated"), true);
        return 1;
    }
    
    private static int addUpgrade(CommandContext<CommandSourceStack> ctx, ToolUpgrade upgrade) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        UpgradeValidationResult validation = upgrade.validate(stack);
        
        switch (validation) {
            case INVALID -> {
                ctx.getSource().sendFailure(
                    Component.literal("Invalid Item for this upgrade, hold a SCGTool to perform this action")
                );
                return 0;
            }
            case WRONG_TIER -> {
                ctx.getSource().sendFailure(
                    Component.literal("Tool tier is too low for this upgrade")
                );
                return 0;
            }
            case WRONG_TOOL -> {
                ctx.getSource().sendFailure(
                    Component.literal("This upgrade cannot be applied to this tool")
                );
                return 0;
            }
            case NO_SLOTS -> {
                ctx.getSource().sendFailure(
                    Component.literal("No available slots in this tool")
                );
                return 0;
            }
            case ALREADY_APPLIED -> {
                ctx.getSource().sendFailure(
                    Component.literal("Current tool already has this upgrade")
                );
                return 0;
            }
        }
        
        upgrade.applyTo(stack);
        ctx.getSource().sendSuccess(() -> Component.literal("Upgrade Applied"), true);
        return 1;
    }
}
