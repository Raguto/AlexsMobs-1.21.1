package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Banner patterns are now fully data-driven in 1.21.
 * Pattern definitions are in data/alexsmobs/banner_pattern/*.json
 * Pattern tags are in data/alexsmobs/tags/banner_pattern/*.json
 */
public class AMBannerRegistry {

    // Empty registry - patterns are data-driven in 1.21
    public static final DeferredRegister<BannerPattern> DEF_REG = DeferredRegister.create(Registries.BANNER_PATTERN, AlexsMobs.MODID);
}
