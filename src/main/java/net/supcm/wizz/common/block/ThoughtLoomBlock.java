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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.supcm.wizz.common.block.entity.ThoughtLoomBlockEntity;
import net.supcm.wizz.common.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ThoughtLoomBlock extends Block implements EntityBlock {
    VoxelShape SHAPE = Stream.of(
            Block.box(0, 15, 0, 16, 16, 16),
            Block.box(0, 0, 0, 16, 3, 16),
            Block.box(1, 3, 1, 15, 15, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public ThoughtLoomBlock() {
        super(Properties.of().strength(7.0f,7.0f).requiresCorrectToolForDrops()
                .lightLevel(state -> 2).noOcclusion());
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ThoughtLoomBlockEntity(pos, state);
    }
    @Override public void onRemove(BlockState state, Level world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof ThoughtLoomBlockEntity te) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(1)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(2)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(3)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hit) {
        if(!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if(world.getBlockEntity(pos) instanceof ThoughtLoomBlockEntity tile) {
                ItemStack handItem = player.getItemInHand(hand);
                if(handItem.getItem() instanceof Items.UnstableGlyphItem ||
                        (handItem.isEmpty() && !tile.handler.getStackInSlot(0).isEmpty() && !player.isCrouching())) {
                    tile.insertOrExtractItem(player, 0);
                    world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS,
                            1.0f, 1.0f);
                }
                else if(handItem.getItem() == Items.CONCEPT_BASE.get())
                    return tile.createConception(player, handItem);
                else {
                    if(!player.isCrouching()) {
                        if(tile.handler.getStackInSlot(1).isEmpty())
                            tile.insertOrExtractItem(player, 1);
                        else if(tile.handler.getStackInSlot(2).isEmpty())
                            tile.insertOrExtractItem(player, 2);
                        else
                            tile.insertOrExtractItem(player, 3);
                        world.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS,
                                1.0f, 1.0f);
                        return InteractionResult.CONSUME;
                    } else {
                        if(!tile.handler.getStackInSlot(3).isEmpty())
                            tile.insertOrExtractItem(player, 3);
                        else if(!tile.handler.getStackInSlot(2).isEmpty())
                            tile.insertOrExtractItem(player, 2);
                        else
                            tile.insertOrExtractItem(player, 1);
                        world.playSound(null, pos, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS,
                                1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if(rand.nextInt(9) < 3) return;
        ThoughtLoomBlockEntity te = null;
        if(world.getBlockEntity(pos) instanceof ThoughtLoomBlockEntity)
            te = (ThoughtLoomBlockEntity) world.getBlockEntity(pos);
        else return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;
        double randomA = rand.nextDouble() - 0.4;
        double randomB = rand.nextDouble() - 0.3;
        if(!te.handler.getStackInSlot(0).isEmpty())
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        if(!te.handler.getStackInSlot(1).isEmpty() ||
                !te.handler.getStackInSlot(2).isEmpty() ||
                !te.handler.getStackInSlot(3).isEmpty())
            world.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
    }
}
