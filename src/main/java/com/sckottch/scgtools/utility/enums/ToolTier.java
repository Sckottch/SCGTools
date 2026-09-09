package com.sckottch.scgtools.utility.enums;

import com.mojang.serialization.Codec;
import com.sckottch.scgtools.item.ModToolMaterials;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;

import java.util.function.IntFunction;

public enum ToolTier implements StringRepresentable {
    FLINT(0, 1, "flint", "Flint", ModToolMaterials.FLINT_MATERIAL, false, false, Rarity.COMMON),
    IRON(1, 2, "iron", "Iron", ToolMaterial.IRON, false, false, Rarity.COMMON),
    DIAMOND(2, 3, "diamond", "Diamond", ToolMaterial.DIAMOND, false, false, Rarity.COMMON),
    NETHERITE(3, 4, "netherite", "Netherite", ToolMaterial.NETHERITE, true, false, Rarity.COMMON),
    DRACONIC(4, 5,"draconic", "Draconic", ModToolMaterials.DRACONIC_MATERIAL, true, true, Rarity.EPIC);

    private final int id;
    private final int durabilityMultiplier;
    private final String serializedName;
    private final String displayName;
    private final ToolMaterial material;
    private final boolean damageResistant;
    private final boolean unbreakable;
    private final Rarity rarity;

    ToolTier(int id, int durabilityMultiplier, String serializedName, String displayName, ToolMaterial material, boolean damageResistant, boolean unbreakable, Rarity rarity) {
        this.id = id;
        this.durabilityMultiplier = durabilityMultiplier;
        this.serializedName = serializedName;
        this.displayName = displayName;
        this.material = material;
        this.damageResistant = damageResistant;
        this.unbreakable = unbreakable;
        this.rarity = rarity;
    }
    
    @Override
    public String getSerializedName() {
        return this.serializedName;
    }
    public int getId() {
        return id;
    }
    public int getDurabilityMultiplier() {
        return durabilityMultiplier;
    }
    public String getDisplayName() {
        return displayName;
    }
    public ToolMaterial getMaterial() {
        return material;
    }
    public boolean isDamageResistant() {
        return damageResistant;
    }
    public boolean isUnbreakable() {
        return unbreakable;
    }
    public Rarity getRarity() {
        return rarity;
    }
    
    public static final Codec<ToolTier> CODEC = StringRepresentable.fromEnum(ToolTier::values);
    public static final IntFunction<ToolTier> BY_ID =
        ByIdMap.continuous(ToolTier::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, ToolTier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ToolTier::getId);
}
