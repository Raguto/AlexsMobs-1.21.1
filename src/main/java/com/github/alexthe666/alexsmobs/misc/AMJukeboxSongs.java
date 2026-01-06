package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.JukeboxSong;

/**
 * Registry keys for Alex's Mobs jukebox songs (music discs)
 * In 1.21, music discs use data-driven JukeboxSong registry entries
 */
public class AMJukeboxSongs {
    public static final ResourceKey<JukeboxSong> THIME = create("thime");
    public static final ResourceKey<JukeboxSong> DAZE = create("daze");

    private static ResourceKey<JukeboxSong> create(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, name));
    }
}
