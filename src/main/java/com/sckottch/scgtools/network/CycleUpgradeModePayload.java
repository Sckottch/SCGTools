package com.sckottch.scgtools.network;

import com.sckottch.scgtools.SCGTools;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CycleUpgradeModePayload() implements CustomPacketPayload {
    public static final CycleUpgradeModePayload INSTANCE = new CycleUpgradeModePayload();
    
    public static final CustomPacketPayload.Type<CycleUpgradeModePayload> TYPE =
        new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(SCGTools.MOD_ID, "cycle_upgrade_mode")
        );
    
    public static final StreamCodec<ByteBuf, CycleUpgradeModePayload> STREAM_CODEC =
        StreamCodec.unit(INSTANCE);
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
