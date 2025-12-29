package com.github.alexthe666.alexsmobs.item.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Data component for alexsmobs:tab_icon to display entities in advancement icons.
 * Replaces the old NBT-based DisplayEntityType system.
 */
public record TabIconDisplay(
        String displayEntityType,
        int displayMobFlags,
        float displayMobScale
) {
    public static final Codec<TabIconDisplay> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("display_entity_type").forGetter(TabIconDisplay::displayEntityType),
            Codec.INT.optionalFieldOf("display_mob_flags", 0).forGetter(TabIconDisplay::displayMobFlags),
            Codec.FLOAT.optionalFieldOf("display_mob_scale", 0.0f).forGetter(TabIconDisplay::displayMobScale)
    ).apply(instance, TabIconDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TabIconDisplay> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, TabIconDisplay::displayEntityType,
            ByteBufCodecs.INT, TabIconDisplay::displayMobFlags,
            ByteBufCodecs.FLOAT, TabIconDisplay::displayMobScale,
            TabIconDisplay::new
    );

    // Convenience constructor with just entity type
    public TabIconDisplay(String displayEntityType) {
        this(displayEntityType, 0, 0.0f);
    }
}
