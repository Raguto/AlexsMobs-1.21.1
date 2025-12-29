package com.github.alexthe666.alexsmobs.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockRedSandCircle extends FallingBlock {

    public static final MapCodec<BlockRedSandCircle> CODEC = simpleCodec(BlockRedSandCircle::new);

    public BlockRedSandCircle(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }
}
