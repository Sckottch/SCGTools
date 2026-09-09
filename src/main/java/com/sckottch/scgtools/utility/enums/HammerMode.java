package com.sckottch.scgtools.utility.enums;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum HammerMode implements StringRepresentable {
    SIZE_3X3(0, 1, 0.75F, ToolTier.FLINT, "size_3x3", "3x3"),
    SIZE_5X5(1, 2, 0.50F, ToolTier.NETHERITE, "size_5x5", "5x5");
    
    private final int id;
    private final int range;
    private final float speedMultiplier;
    private final ToolTier minTier;
    private final String serializedName;
    private final String displayName;
    
    HammerMode(int id, int range, float speedMultiplier, ToolTier minTier, String serializedName, String displayName) {
        this.id = id;
        this.range = range;
        this.speedMultiplier = speedMultiplier;
        this.minTier = minTier;
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
    public int getRange() {
        return range;
    }
    public float getSpeedMultiplier() {
        return speedMultiplier;
    }
    public ToolTier getMinTier() {
        return minTier;
    }
    public String getDisplayName() {
        return displayName;
    }
    
    public static final Codec<HammerMode> CODEC = StringRepresentable.fromEnum(HammerMode::values);
    public static final IntFunction<HammerMode> BY_ID =
        ByIdMap.continuous(HammerMode::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, HammerMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, HammerMode::getId);
}