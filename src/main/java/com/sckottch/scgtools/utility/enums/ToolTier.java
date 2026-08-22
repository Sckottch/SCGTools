package com.sckottch.scgtools.utility.enums;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum ToolTier implements StringRepresentable {
    FLINT(0, 120, 1, "flint", "Flint"),
    IRON(1, 250, 2, "iron", "Iron"),
    DIAMOND(2, 1500, 3, "diamond", "Diamond"),
    NETHERITE(3, 2500, 4, "netherite", "Netherite"),
    DRACONIC(4, 4000, 5,"draconic", "Draconic");
    
    private final int id;
    private final int baseDurability;
    private final int durabilityMultiplier;
    private final String serializedName;
    private final String displayName;
    
    ToolTier(int id, int baseDurability, int durabilityMultiplier, String serializedName, String displayName) {
        this.id = id;
        this.baseDurability = baseDurability;
        this.durabilityMultiplier = durabilityMultiplier;
        this.serializedName = serializedName;
        this.displayName = displayName;
    }
    
    public int getId() {
        return id;
    }
    
    public int getBaseDurability() {
        return baseDurability;
    }
    
    public int getDurabilityMultiplier() {
        return durabilityMultiplier;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String getSerializedName() {
        return this.serializedName;
    }
    
    public static final Codec<ToolTier> CODEC = StringRepresentable.fromEnum(ToolTier::values);
    public static final IntFunction<ToolTier> BY_ID =
        ByIdMap.continuous(ToolTier::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, ToolTier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ToolTier::getId);
}