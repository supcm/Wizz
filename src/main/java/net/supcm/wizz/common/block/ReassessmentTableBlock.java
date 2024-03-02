package net.supcm.wizz.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.supcm.wizz.common.block.entity.ReassessmentTableBlockEntity;
import net.supcm.wizz.common.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ReassessmentTableBlock extends Block implements EntityBlock {
    VoxelShape SHAPE = Stream.of(
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(2, 2, 2, 14, 6, 14),
            Block.box(0, 6, 0, 16, 14, 16),
            Block.box(0, 14, 0, 1, 18, 1),
            Block.box(15, 14, 0, 16, 18, 1),
            Block.box(15, 14, 15, 16, 18, 16),
            Block.box(0, 14, 15, 1, 18, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public ReassessmentTableBlock() {
        super(Properties.of().strength(2.0f, 2.0f).requiresCorrectToolForDrops());
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL;}

    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReassessmentTableBlockEntity(pos, state);
    }
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if(rand.nextInt(9) < 3) return;
        if(world.getBlockEntity(pos) instanceof ReassessmentTableBlockEntity tile) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 1.0D;
            double z = pos.getZ() + 0.5D;
            double randomA = rand.nextInt(2) - 0.4;
            double randomB = rand.nextInt(2) - 0.3;
            if(tile.isValid){
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                        0.0D, 0.025D, 0.0D);
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_,
                                                                  BlockEntityType<T> p_153214_) {
        return (level, pos, state, blockEntity) -> ((ReassessmentTableBlockEntity)blockEntity).tick();
    }

    @Override public void onRemove(BlockState state, Level world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof ReassessmentTableBlockEntity te) {
            te.invalidatePillars();
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if(!world.isClientSide) {
            if(hand == InteractionHand.MAIN_HAND && world.getBlockEntity(pos) instanceof ReassessmentTableBlockEntity tile) {
                ItemStack handItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                if(!tile.isValid) {
                    player.displayClientMessage(
                            Component.translatable("tile.wizz.reassessment_table.not_valid"),
                            true);
                    return InteractionResult.FAIL;
                }
                else {
                    tile.updateRecipe();
                    if(handItem.getItem() != Items.CRYSTAL.get() && handItem.getItem() != Items.WIZZ.get()) {
                        tile.insertOrExtractItem(player, 0);
                        world.playSound(null, pos, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS,
                                1.0f, 1.0f);
                    } else if(tile.getRecipe() != null){
                        tile.createResult();
                        if(!player.isCreative())
                            handItem.shrink(1);
                        world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS,
                                1.0f, 1.0f);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
