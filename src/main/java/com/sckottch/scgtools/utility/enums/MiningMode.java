package com.sckottch.scgtools.utility.enums;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum MiningMode implements StringRepresentable {
    NORMAL(0, "normal", "Normal"),
    HAMMER(1, "hammer", "Hammer"),
    SPECIAL(2, "special", "Special");

    
    private final int id;
    private final String serializedName;
    private final String displayName;
    
    MiningMode(int id, String serializedName, String displayName) {
        this.id = id;
        this.serializedName = serializedName;
        this.displayName = displayName;
    }
    
    @Override
    public String getSerializedName() {
        return this.serializedName;
    }
    
    public int getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static final Codec<MiningMode> CODEC = StringRepresentable.fromEnum(MiningMode::values);
    public static final IntFunction<MiningMode> BY_ID =
        ByIdMap.continuous(MiningMode::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, MiningMode> STREAM_CODEC =
        ByteBufCodecs.idMapper(BY_ID, MiningMode::getId);
}
