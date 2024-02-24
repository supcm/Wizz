package net.supcm.wizz.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.supcm.wizz.common.block.entity.ReassessmentPillarBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ReassessmentPillarBlock extends Block implements EntityBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(2, 2, 2, 14, 14, 14),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 14, 0, 16, 16, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public ReassessmentPillarBlock() {
        super(Properties.of().strength(2.0f, 2.0f).requiresCorrectToolForDrops());
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReassessmentPillarBlockEntity(pos, state);
    }
    @Override public void onRemove(BlockState state, Level world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof ReassessmentPillarBlockEntity te) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hit) {
        if(!world.isClientSide) {
            if(hand == InteractionHand.MAIN_HAND) {
                if(world.getBlockEntity(pos) instanceof ReassessmentPillarBlockEntity tile) {
                    tile.insertOrExtractItem(player, 0);
                    world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
        } else {
            if(world.getBlockEntity(pos) instanceof ReassessmentPillarBlockEntity tile) {
                if(!tile.handler.getStackInSlot(0).isEmpty()){
                    double x = pos.getX() + 0.5D;
                    double y = pos.getY() + 1.25D;
                    double z = pos.getZ() + 0.5D;
                    world.addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, 0.0D, 0.025D,
                            0.0D);
                }
            }
        }
        return InteractionResult.PASS;
    }
}
