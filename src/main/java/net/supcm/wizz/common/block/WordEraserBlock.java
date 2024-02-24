package net.supcm.wizz.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.supcm.wizz.common.block.entity.WordEraserBlockEntity;
import net.supcm.wizz.common.block.entity.WordForgeBlockEntity;
import net.supcm.wizz.common.block.entity.WordMachineBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class WordEraserBlock extends Block implements EntityBlock {
    VoxelShape SHAPE = Stream.of(
            Block.box(0, 16, 14, 2, 17, 16),
            Block.box(-2, 0, -2, 18, 2, 18),
            Block.box(0, 2, 0, 2, 10, 2),
            Block.box(14, 2, 0, 16, 10, 2),
            Block.box(14, 2, 14, 16, 10, 16),
            Block.box(0, 2, 14, 2, 10, 16),
            Block.box(0, 10, 0, 16, 16, 16),
            Block.box(0, 16, 0, 2, 17, 2),
            Block.box(14, 16, 0, 16, 17, 2),
            Block.box(14, 16, 14, 16, 17, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public WordEraserBlock() {
        super(Properties.of().strength(12.0f, 21.0f).requiresCorrectToolForDrops());
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
    @Override public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WordEraserBlockEntity(pos, state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (level, pos, state, blockEntity) -> ((WordEraserBlockEntity)blockEntity).tick();
    }

    @Override public void onRemove(BlockState state, Level world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof WordEraserBlockEntity te) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hit) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof WordEraserBlockEntity tile) {
            ItemStack handItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(hand == InteractionHand.MAIN_HAND) {
                if(handItem.getItem() != Items.BOOK) {
                    tile.insertOrExtractItem(player, 0);
                    world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1f, 1f);
                    return InteractionResult.CONSUME;
                } else {
                    if(!tile.handler.getStackInSlot(0).isEmpty() && tile.handler.getStackInSlot(0).isEnchanted()){
                        tile.proceedErasing();
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        } else
            return InteractionResult.CONSUME;
        return InteractionResult.PASS;
    }
}
