package com.sckottch.scgtools.utility.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.EvolvingPicaxe;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = SCGTools.MODID)
public class ModCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> tierNode = Commands.literal("tier");
        
        for (ToolTier tier : ToolTier.values()) {
            tierNode.then(
                Commands.literal(tier.getDisplayName())
                    .executes(ctx -> setTier(ctx, tier))
            );
        }
        
        
        event.getDispatcher().register(
            Commands.literal("scgtools")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(tierNode)
                .then(Commands.literal("refresh").executes(ModCommands::refresh))
            
        );
    }
    
    private static int setTier(CommandContext<CommandSourceStack> ctx, ToolTier tier)
    throws CommandSyntaxException
    {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        
        if (!(stack.getItem() instanceof EvolvingPicaxe)) {
            ctx.getSource().sendFailure(
                Component.literal("Hold a SCG Tool in your main hand to perform this command")
            );
            return 0;
        }
        
        EvolvingPicaxe.applyTier(stack, tier, player.level());
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Changed weapon tier to: " + tier.getDisplayName()), true
        );
        return 1;
    }
    
    private static int refresh(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        
        if (!(stack.getItem() instanceof EvolvingPicaxe)) {
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
        
        EvolvingPicaxe.applyTier(stack, tier, player.level());
        
        ctx.getSource().sendSuccess(() -> Component.literal("Tool Updated"), true);
        return 1;
    }
}
