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
import net.minecraft.world.item.*;
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
import net.supcm.wizz.common.block.entity.WordForgeBlockEntity;
import net.supcm.wizz.common.item.Items;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class WordForgeBlock extends Block implements EntityBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(1, 14, 1, 15, 18, 15),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(-2, 8, 4, 0, 10, 12),
            Block.box(16, 8, 4, 18, 10, 12),
            Block.box(4, 8, -2, 12, 10, 0),
            Block.box(4, 8, 16, 12, 10, 18),
            Block.box(3, 10, 3, 13, 14, 13),
            Block.box(0, 10, 14, 2, 11, 16),
            Block.box(14, 10, 0, 16, 11, 2),
            Block.box(0, 10, 0, 2, 11, 2),
            Block.box(14, 10, 14, 16, 11, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public WordForgeBlock() {
        super(Properties.of().strength(12.0f, 21.0f).requiresCorrectToolForDrops());
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
    @Override
    public RenderShape getRenderShape(BlockState p_60550_) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WordForgeBlockEntity(pos, state);
    }
    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof WordForgeBlockEntity te) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(1)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(2)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hit) {
        if(!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if(world.getBlockEntity(pos) instanceof WordForgeBlockEntity tile) {
                ItemStack handItem = player.getItemInHand(hand);
                double hitLoc = hit.getLocation().y;
                boolean up = Math.abs(hitLoc-pos.getY()) >= 0.9D;
                boolean low = Math.abs(hitLoc-pos.getY()) <= 0.45D;
                if(handItem.isEmpty() || handItem.getItem() instanceof Items.GlyphItem) {
                    if(up) tile.insertOrExtractItem(player, 0);
                    else if(low) tile.insertOrExtractItem(player, 2);
                    else tile.insertOrExtractItem(player, 1);
                    world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1f, 1f);
                    boolean isTier2 = !tile.handler.getStackInSlot(0).isEmpty()
                            && !tile.handler.getStackInSlot(1).isEmpty();
                    boolean isTier3 = !tile.handler.getStackInSlot(0).isEmpty() &&
                            !tile.handler.getStackInSlot(1).isEmpty() &&
                            !tile.handler.getStackInSlot(2).isEmpty();
                    if(isTier3) {
                        tile.getEnchLevel(2);
                    } else if(isTier2) {
                        tile.getEnchLevel(1);;
                    } else {
                        tile.getEnchLevel(0);
                    }
                } else {
                    if (handItem.getItem() == net.minecraft.world.item.Items.BOOK ||
                            handItem.getItem() instanceof DiggerItem ||
                            handItem.getItem() instanceof SwordItem ||
                            handItem.getItem() instanceof BowItem ||
                            handItem.getItem() instanceof CrossbowItem ||
                            handItem.getItem() instanceof TridentItem) {
                        boolean isTier2 = !tile.handler.getStackInSlot(0).isEmpty()
                                && !tile.handler.getStackInSlot(1).isEmpty() &&
                                tile.handler.getStackInSlot(2).isEmpty();
                        boolean isTier3 = !tile.handler.getStackInSlot(0).isEmpty() &&
                                !tile.handler.getStackInSlot(1).isEmpty() &&
                                !tile.handler.getStackInSlot(2).isEmpty();
                        if (isTier3) {
                            return tile.enchantBook(player, handItem, 2);
                        } else if (isTier2) {
                            return tile.enchantBook(player, handItem, 1);
                        } else {
                            if(!tile.handler.getStackInSlot(0).isEmpty()
                                    && tile.handler.getStackInSlot(1).isEmpty()
                                    && tile.handler.getStackInSlot(2).isEmpty()) {
                                return tile.enchantBook(player, handItem, 0);
                            } else if(!tile.handler.getStackInSlot(1).isEmpty()
                                    && tile.handler.getStackInSlot(0).isEmpty()
                                    && tile.handler.getStackInSlot(2).isEmpty()){
                                return tile.enchantBook(player, handItem, 0);
                            } else if(!tile.handler.getStackInSlot(2).isEmpty()
                                    && tile.handler.getStackInSlot(1).isEmpty()
                                    && tile.handler.getStackInSlot(0).isEmpty()) {
                                return tile.enchantBook(player, handItem, 0);
                            }
                        }
                    } else
                        tile.enchantItem(player, handItem);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if(rand.nextInt(9) < 3) return;
        WordForgeBlockEntity te = null;
        if(world.getBlockEntity(pos) instanceof WordForgeBlockEntity)
            te = (WordForgeBlockEntity) world.getBlockEntity(pos);
        else return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.35D;
        double z = pos.getZ() + 0.5D;
        double randomA = rand.nextDouble() - 0.4;
        double randomB = rand.nextDouble() - 0.3;
        if((!te.handler.getStackInSlot(0).isEmpty() || !te.handler.getStackInSlot(1).isEmpty() ||
                !te.handler.getStackInSlot(2).isEmpty()) &&
                !(!te.handler.getStackInSlot(0).isEmpty() && !te.handler.getStackInSlot(1).isEmpty() &&
                        !te.handler.getStackInSlot(2).isEmpty()))
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        if(!te.handler.getStackInSlot(1).isEmpty() && !te.handler.getStackInSlot(0).isEmpty() &&
                !te.handler.getStackInSlot(2).isEmpty())
            world.addParticle(ParticleTypes.SOUL,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        world.addParticle(ParticleTypes.ENCHANT, x + randomA, y + randomA-randomB,
                z + randomB, 0.0D, 0.015D, 0.0D);
    }
}
