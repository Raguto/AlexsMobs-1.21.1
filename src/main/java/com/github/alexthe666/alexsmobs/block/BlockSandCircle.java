package com.github.alexthe666.alexsmobs.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockSandCircle extends FallingBlock {

    public static final MapCodec<BlockSandCircle> CODEC = simpleCodec(BlockSandCircle::new);

    public BlockSandCircle(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }
}
