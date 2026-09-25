package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class TeleportUpgrade extends ToolUpgrade implements UseUpgrade {
    private static final int COOLDOWN_TICKS = 20;
    private static final double BLOCKS_PER_TIER = 2.0;
    private static final double STEP = 0.25;
    private static final double MIN_DISTANCE = 1.0;
    
    public TeleportUpgrade(ToolTier minTier, String displayName, TagKey<Item> applicableTo) {
        super(minTier, displayName, applicableTo);
    }
    
    @Override
    public void onApply(ItemStack stack) {
        stack.set(ModDataComponents.TELEPORT_ENABLED.get(), true);
    }

    @Override
    public boolean isEnabled(ItemStack tool) {
        return tool.getOrDefault(ModDataComponents.TELEPORT_ENABLED.get(), false);
    }

    @Override
    public void toggle(ItemStack tool) {
        tool.set(ModDataComponents.TELEPORT_ENABLED.get(), !isEnabled(tool));
    }

    public double getRange(ItemStack tool) {
        ToolTier tier = tool.get(ModDataComponents.TOOL_TIER.get());
        return tier == null ? 0 : BLOCKS_PER_TIER * (tier.getId() + 1);
    }
    
    @Override
    public InteractionResult onUse(Level level, Player player, InteractionHand hand, ItemStack tool) {
        ItemStack pearl = findPearl(player);
        if (pearl.isEmpty()) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendOverlayMessage(Component.translatable("message.scgtools.teleport.no_pearl"));
            }
            return InteractionResult.PASS;
        }
        
        Vec3 destination = findDestination(level, player, getRange(tool));
        if (destination == null) return InteractionResult.PASS;
        
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            Vec3 origin = serverPlayer.position();
            
            effects(serverLevel, origin);
            serverPlayer.teleportTo(destination.x, destination.y, destination.z);
            serverPlayer.resetFallDistance();
            effects(serverLevel, destination);
            
            pearl.consume(1, serverPlayer);
            serverPlayer.getCooldowns().addCooldown(tool, COOLDOWN_TICKS);
        }
        
        return InteractionResult.CONSUME;
    }
    
    private static ItemStack findPearl(Player player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < Inventory.SELECTION_SIZE; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(Items.ENDER_PEARL)) return stack;
        }
        
        ItemStack offhand = player.getOffhandItem();
        return offhand.is(Items.ENDER_PEARL) ? offhand : ItemStack.EMPTY;
    }
    
    private static @Nullable Vec3 findDestination(Level level, Player player, double range) {
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eye.add(look.scale(range));
        double eyeHeight = eye.y - player.getY();
        
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        
        if (hit.getType() == HitResult.Type.BLOCK && hit.getDirection() == Direction.UP) {
            Vec3 feet = hit.getLocation();
            if (fits(level, player, feet)) return feet;
        }
        
        double distance = hit.getLocation().distanceTo(eye);
        for (double d = distance; d >= MIN_DISTANCE; d -= STEP) {
            Vec3 feet = eye.add(look.scale(d)).subtract(0, eyeHeight, 0);
            if (fits(level, player, feet)) return feet;
        }
        
        return null;
    }
    
    private static boolean fits(Level level, Player player, Vec3 feet) {
        AABB box = player.getBoundingBox().move(feet.subtract(player.position()));
        return level.noCollision(player, box);
    }
    
    private static void effects(ServerLevel level, Vec3 pos) {
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
        level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y + 1.0, pos.z, 32, 0.3, 0.6, 0.3, 0.1);
    }
}