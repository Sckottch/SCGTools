package com.sckottch.scgtools.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ToolLevelData(int level, int xp) {
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 10;
    public static final int BASE_SLOTS = 3;
    
    public static final ToolLevelData INITIAL = new ToolLevelData(MIN_LEVEL, 0);
    
    public int slotCount () {
        int slots = BASE_SLOTS;
        if (level >= 5) slots++;
        if (level >= 10) slots++;
        return slots;
    }
    
    public static final Codec<ToolLevelData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("level").forGetter(ToolLevelData::level),
            Codec.INT.fieldOf("xp").forGetter(ToolLevelData::xp)
        ).apply(instance, ToolLevelData::new)
    );
    
    public static final StreamCodec<ByteBuf, ToolLevelData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, ToolLevelData::level,
        ByteBufCodecs.INT, ToolLevelData::xp,
        ToolLevelData::new
    );
}
