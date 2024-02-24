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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
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
import net.supcm.wizz.common.block.entity.MatrixBlockEntity;
import net.supcm.wizz.common.item.Items;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class MatrixBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(4, 14, 4, 12, 18, 12),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 10, 0, 16, 12, 16),
            Block.box(-2, 12, -2, 18, 14, 18),
            Block.box(1, 2, 1, 15, 10, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public MatrixBlock() {
        super(Properties.of().strength(7.0f, 7.0f).requiresCorrectToolForDrops());
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MatrixBlockEntity(pos, state);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? (level0, pos0, state0, blockEntity) -> ((MatrixBlockEntity)blockEntity).clientTick()
        : (level0, pos0, state0, blockEntity) -> ((MatrixBlockEntity)blockEntity).tick();
    }

    @Override public void onRemove(BlockState state, Level world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof MatrixBlockEntity te) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(1)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hit) {
        if(!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if(world.getBlockEntity(pos) instanceof MatrixBlockEntity tile) {
                ItemStack handItem = player.getItemInHand(hand);
                double hitLoc = hit.getLocation().y;
                boolean up = hitLoc-(int)hitLoc >= 0.75d || (int)hitLoc > pos.getY();
                if(!tile.doCraft){
                    if (up && (handItem.isEmpty() || handItem.getItem() == Items.PLATE.get())) {
                        tile.insertOrExtractItem(player, 0);
                        world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1f, 1f);
                    } else if (handItem.isEmpty() || (handItem.getItem() instanceof Items.GlyphItem
                            || handItem.getItem() instanceof Items.UnstableGlyphItem)) {
                        tile.insertOrExtractItem(player, 1);
                        world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1f, 1f);
                    } else if(handItem.getItem() == Items.LAVA_CRYSTAL.get()){
                        if (!tile.handler.getStackInSlot(0).isEmpty()) {
                            if (!player.isCreative()) {
                                if (player.experienceLevel < 3 && !player.isCreative()) {
                                    player.displayClientMessage(Component.translatable("enchanting.notenoughxp",
                                            0), true);
                                    return InteractionResult.PASS;
                                }
                                handItem.shrink(1);
                                player.onEnchantmentPerformed(handItem, 3);
                            }
                            world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL,
                                    SoundSource.BLOCKS, 1f, 1f);
                            tile.setDoCraft(true);
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if(rand.nextInt(9) < 3) return;
        MatrixBlockEntity te;
        if(world.getBlockEntity(pos) instanceof MatrixBlockEntity)
            te = (MatrixBlockEntity) world.getBlockEntity(pos);
        else return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;
        double randomA = rand.nextDouble() - 0.4;
        double randomB = rand.nextDouble() - 0.3;
        if(te.doCraft)
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        world.addParticle(ParticleTypes.ENCHANT, x + randomA, y + randomA-randomB,
                z + randomB, 0.0D, 0.015D, 0.0D);
        world.playSound(null, pos, SoundEvents.SOUL_ESCAPE,
                SoundSource.BLOCKS,1.0F, 1.0F);
    }
}
