package com.sckottch.scgtools.utility.enums;

import com.mojang.serialization.Codec;
import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.item.ModToolMaterials;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ToolMaterial;

import java.util.function.IntFunction;

public enum ToolTier implements StringRepresentable {
    FLINT(0, 1, "flint", "Flint", ModToolMaterials.FLINT_MATERIAL ),
    IRON(1, 2, "iron", "Iron", ModToolMaterials.IRON_MATERIAL),
    DIAMOND(2, 3, "diamond", "Diamond", ModToolMaterials.FLINT_MATERIAL),
    NETHERITE(3, 4, "netherite", "Netherite", ModToolMaterials.FLINT_MATERIAL),
    DRACONIC(4, 5,"draconic", "Draconic", ModToolMaterials.FLINT_MATERIAL);
    
    private final int id;
    private final int durabilityMultiplier;
    private final String serializedName;
    private final String displayName;
    private final ToolMaterial material;
    
    ToolTier(int id, int durabilityMultiplier, String serializedName, String displayName, ToolMaterial material) {
        this.id = id;
        this.durabilityMultiplier = durabilityMultiplier;
        this.serializedName = serializedName;
        this.displayName = displayName;
        this.material = material;
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
    public Identifier getModelId() {
        return Identifier.fromNamespaceAndPath(SCGTools.MODID, "pickaxe_" + this.getSerializedName());
    }
    
    public static final Codec<ToolTier> CODEC = StringRepresentable.fromEnum(ToolTier::values);
    public static final IntFunction<ToolTier> BY_ID =
        ByIdMap.continuous(ToolTier::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, ToolTier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ToolTier::getId);
}