package com.github.alexthe666.alexsmobs.item.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Data component for storing the Dimensional Carver's portal position.
 */
public record CarverPortalPos(double x, double y, double z, boolean active) {
    
    public static final CarverPortalPos EMPTY = new CarverPortalPos(0, 0, 0, false);
    
    public static final Codec<CarverPortalPos> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.DOUBLE.fieldOf("x").forGetter(CarverPortalPos::x),
            Codec.DOUBLE.fieldOf("y").forGetter(CarverPortalPos::y),
            Codec.DOUBLE.fieldOf("z").forGetter(CarverPortalPos::z),
            Codec.BOOL.fieldOf("active").forGetter(CarverPortalPos::active)
        ).apply(instance, CarverPortalPos::new)
    );
    
    public static final StreamCodec<ByteBuf, CarverPortalPos> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE, CarverPortalPos::x,
        ByteBufCodecs.DOUBLE, CarverPortalPos::y,
        ByteBufCodecs.DOUBLE, CarverPortalPos::z,
        ByteBufCodecs.BOOL, CarverPortalPos::active,
        CarverPortalPos::new
    );
}
