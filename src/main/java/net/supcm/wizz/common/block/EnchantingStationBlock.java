package net.supcm.wizz.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.supcm.wizz.common.block.entity.EnchantingStationBlockEntity;
import net.supcm.wizz.common.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class EnchantingStationBlock extends Block implements EntityBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(0, 0, 0, 16, 1, 16),
            Block.box(-2, 9, -2, 18, 11, 18),
            Block.box(0, 1, 7, 16, 9, 9),
            Block.box(7, 1, 0, 9, 9, 16),
            Block.box(6, 11, 6, 10, 15, 10)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public EnchantingStationBlock() {
        super(Properties.of().strength(5.0f, 5.0f).requiresCorrectToolForDrops());
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantingStationBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return (level0, pos0, state0, blockEntity) -> ((EnchantingStationBlockEntity)blockEntity).tick();
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof EnchantingStationBlockEntity te) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(1)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if(!world.isClientSide) {
            if(hand == InteractionHand.MAIN_HAND) {
                if(world.getBlockEntity(pos) instanceof EnchantingStationBlockEntity tile) {
                    double hitLoc = hit.getLocation().y;
                    boolean up = Math.abs(hitLoc - pos.getY()) >= 0.45;
                    if(!tile.doCraft){
                        if (up) tile.insertOrExtractItem(player, 0);
                        else tile.insertOrExtractItem(player, 1);
                        world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL,
                                SoundSource.BLOCKS, 1f, 1f);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        } else {
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 1.25D;
            double z = pos.getZ() + 0.5D;
            world.addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, 0.0D, 0.025D,
                    0.0D);

        }
        return InteractionResult.PASS;
    }

    @Override public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if(rand.nextInt(9) < 3) return;
        EnchantingStationBlockEntity te;
        if(world.getBlockEntity(pos) instanceof EnchantingStationBlockEntity)
            te = (EnchantingStationBlockEntity) world.getBlockEntity(pos);
        else return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;
        double randomA = rand.nextDouble() - 0.4;
        double randomB = rand.nextDouble() - 0.3;
        if(te.doCraft) {
            for(int i = 0; i < (rand.nextInt(4) + 1); i++) {
                world.addParticle(ParticleTypes.SOUL,
                        x + randomA, y + 0.65 + randomA - randomB, z + randomB,
                        0.0D, -0.025D, 0.0D);
                if(te.handler.getStackInSlot(1).getItem() == Items.FIR.get())
                    world.addParticle(ParticleTypes.CRIT,
                            x + randomA, y + 0.65 + randomA - randomB, z + randomB,
                            0.0D, -0.025D, 0.0D);
            }
        }
    }
}
