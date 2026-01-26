package com.github.alexthe666.alexsmobs.block;

import com.mojang.serialization.MapCodec;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockCapsid extends BaseEntityBlock {
    public static final MapCodec<BlockCapsid> CODEC = simpleCodec(props -> new BlockCapsid());

    public static final DirectionProperty HORIZONTAL_FACING = HorizontalDirectionalBlock.FACING;
    public BlockCapsid() {
        super(Properties.of().mapColor(MapColor.COLOR_PURPLE).noOcclusion().isValidSpawn(BlockCapsid::spawnOption).isRedstoneConductor(BlockCapsid::isntSolid).sound(SoundType.GLASS).lightLevel((state) -> 5).requiresCorrectToolForDrops().strength(1.5F));
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return (BlockState)p_185499_1_.setValue(HORIZONTAL_FACING, p_185499_2_.rotate((Direction)p_185499_1_.getValue(HORIZONTAL_FACING)));
    }

    public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.rotate(p_185471_2_.getRotation((Direction)p_185471_1_.getValue(HORIZONTAL_FACING)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    private static Boolean spawnOption(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
        return (boolean)false;
    }

    private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean skipRendering(BlockState p_200122_1_, BlockState p_200122_2_, Direction p_200122_3_) {
        return p_200122_2_.getBlock() == this ? true : super.skipRendering(p_200122_1_, p_200122_2_, p_200122_3_);
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        return tryInsertItem(worldIn, pos, player, handIn, state);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        if (worldIn.getBlockEntity(pos) instanceof TileEntityCapsid capsid && !capsid.getItem(0).isEmpty()) {
            popResource(worldIn, pos, capsid.getItem(0).copy());
            capsid.setItem(0, ItemStack.EMPTY);
            return InteractionResult.sidedSuccess(worldIn.isClientSide);
        }
        return super.useWithoutItem(state, worldIn, pos, player, hit);
    }

    public static InteractionResult tryInsertItem(Level worldIn, BlockPos pos, @Nullable Player player, InteractionHand handIn, BlockState state) {
        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack heldItem = player.getItemInHand(handIn);
        if (!(worldIn.getBlockEntity(pos) instanceof TileEntityCapsid capsid)) {
            return InteractionResult.PASS;
        }
        if (heldItem.isEmpty()) {
            if (!capsid.getItem(0).isEmpty()) {
                popResource(worldIn, pos, capsid.getItem(0).copy());
                capsid.setItem(0, ItemStack.EMPTY);
                return InteractionResult.sidedSuccess(worldIn.isClientSide);
            }
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        Item capsidItem = state.getBlock().asItem();
        if (heldItem.getItem() == capsidItem) {
            return InteractionResult.PASS;
        }
        ItemStack copy = heldItem.copy();
        copy.setCount(1);
        if (capsid.getItem(0).isEmpty()) {
            capsid.setItem(0, copy);
            if (!player.isCreative()) {
                heldItem.shrink(1);
            }
            triggerCapsidAdvancement(player, copy);
            return InteractionResult.sidedSuccess(worldIn.isClientSide);
        } else if (ItemStack.isSameItem(capsid.getItem(0), copy) && capsid.getItem(0).getMaxStackSize() > capsid.getItem(0).getCount() + copy.getCount()) {
            capsid.getItem(0).grow(1);
            if (!player.isCreative()) {
                heldItem.shrink(1);
            }
            triggerCapsidAdvancement(player, copy);
            return InteractionResult.sidedSuccess(worldIn.isClientSide);
        } else {
            popResource(worldIn, pos, capsid.getItem(0).copy());
            capsid.setItem(0, ItemStack.EMPTY);
            return InteractionResult.sidedSuccess(worldIn.isClientSide);
        }
    }

    private static void triggerCapsidAdvancement(Player player, ItemStack inserted) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!inserted.is(AMItemRegistry.MOSQUITO_LARVA.get())) {
            return;
        }
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath("alexsmobs", "alexsmobs/capsid");
        var advancement = serverPlayer.server.getAdvancements().get(advancementId);
        if (advancement == null) {
            return;
        }
        var progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
                serverPlayer.getAdvancements().award(advancement, criterion);
            }
        }
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof TileEntityCapsid) {
            Containers.dropContents(worldIn, pos, (TileEntityCapsid) tileentity);
            worldIn.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }


    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityCapsid(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, AMTileEntityRegistry.CAPSID.get(), TileEntityCapsid::commonTick);
    }
}
