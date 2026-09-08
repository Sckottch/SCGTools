package com.sckottch.scgtools.forge.operation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.sckottch.scgtools.utility.enums.UpgradeValidationResult;
import com.sckottch.scgtools.utility.registries.ModRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public interface ForgeOperation {
    Codec<ForgeOperation> CODEC = ModRegistries.OPERATION_REGISTRY.byNameCodec().dispatch(
        ForgeOperation::codec,
        codec -> codec
    );
    
    StreamCodec<RegistryFriendlyByteBuf, ForgeOperation> STREAM_CODEC =
        ByteBufCodecs.fromCodecWithRegistries(CODEC);
    
    MapCodec<? extends ForgeOperation> codec();

    UpgradeValidationResult validate(ItemStack tool);
    void apply(ItemStack tool, HolderLookup.Provider lookup);

    default boolean canApply(ItemStack tool) {
        return validate(tool).isOk();
    }

    // distingue duas receitas da mesma operação; o prefixo do id vem do nome registrado
    String descriptor();

    default Identifier defaultRecipeId() {
        Identifier type = ModRegistries.OPERATION_REGISTRY.getKey(codec());
        return Identifier.fromNamespaceAndPath(type.getNamespace(), type.getPath() + "_" + descriptor());
    }
}
