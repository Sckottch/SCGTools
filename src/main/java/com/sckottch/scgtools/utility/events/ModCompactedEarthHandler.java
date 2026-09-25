package com.sckottch.scgtools.utility.events;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.block.CompactedEarthBlock;
import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.item.tools.upgrades.CompactorUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SCGTools.MOD_ID)
public class ModCompactedEarthHandler {
    private static final Identifier SPEED_ID = Identifier.fromNamespaceAndPath(SCGTools.MOD_ID, "compacted_earth_speed");

    // instâncias fixas por nível: o addOrUpdateTransientModifier compara por referência,
    // então um modificador novo a cada tick marcaria o atributo para reenvio todo tick
    private static final AttributeModifier[] MODIFIERS = new AttributeModifier[5];

    static {
        for (int level = 1; level < MODIFIERS.length; level++) {
            MODIFIERS[level] = new AttributeModifier(
                SPEED_ID, CompactorUpgrade.bonusPercent(level) / 100.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Entity vehicle = player.getVehicle();
        if (vehicle == null) {
            update(player);
            return;
        }

        remove(player);
        if (vehicle instanceof LivingEntity mount) {
            update(mount);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity instanceof Player) return;
        if (entity.level().isClientSide()) return;

        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null || !speed.hasModifier(SPEED_ID)) return;

        boolean ridden = entity.getPassengers().stream().anyMatch(passenger -> passenger instanceof Player);
        if (!ridden) {
            speed.removeModifier(SPEED_ID);
        }
    }

    private static void update(LivingEntity entity) {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null || !entity.onGround()) return;

        int level = roadLevel(entity);
        if (level == 0) {
            speed.removeModifier(SPEED_ID);
        } else {
            speed.addOrUpdateTransientModifier(MODIFIERS[level]);
        }
    }

    private static void remove(LivingEntity entity) {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SPEED_ID);
        }
    }

    private static int roadLevel(LivingEntity entity) {
        BlockPos on = entity.getOnPos();
        BlockState state = entity.level().getBlockState(on);
        if (!state.is(ModBlocks.COMPACTED_EARTH.get())) {
            state = entity.level().getBlockState(on.below());
        }

        return state.is(ModBlocks.COMPACTED_EARTH.get()) ? state.getValue(CompactedEarthBlock.LEVEL) : 0;
    }
}
