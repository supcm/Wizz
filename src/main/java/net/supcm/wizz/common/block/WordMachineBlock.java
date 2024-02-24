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
import net.supcm.wizz.common.block.entity.WordMachineBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.stream.Stream;

public class WordMachineBlock extends Block implements EntityBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(0, 0, 0, 16, 12, 16),
            Block.box(2, 12, 2, 14, 18, 14),
            Block.box(0, 12, 0, 2, 19, 2),
            Block.box(14, 12, 14, 16, 19, 16),
            Block.box(14, 12, 0, 16, 19, 2),
            Block.box(0, 12, 14, 2, 19, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public WordMachineBlock() {
        super(Properties.of().strength(7.0f, 15.0f).requiresCorrectToolForDrops());
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WordMachineBlockEntity(pos, state);
    }
    @Override public void onRemove(BlockState state, Level world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof WordMachineBlockEntity te) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(1)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hit) {
        if(!world.isClientSide) {
            if(hand == InteractionHand.MAIN_HAND) {
                WordMachineBlockEntity tile = (WordMachineBlockEntity)world.getBlockEntity(pos);
                ItemStack handItem = player.getItemInHand(hand);
                double hitLoc = hit.getLocation().y;
                boolean up = hitLoc-(int)hitLoc >= 0.75d || (int)hitLoc > pos.getY();
                if(handItem.isEmpty() || handItem.getItem() instanceof net.supcm.wizz.common.item.Items.GlyphItem){
                    if(up) tile.insertOrExtractItem(player, 0);
                    else tile.insertOrExtractItem(player, 1);
                    world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1f, 1f);
                    boolean isTier2 = !tile.handler.getStackInSlot(0).isEmpty()
                            && !tile.handler.getStackInSlot(1).isEmpty();
                    if(isTier2) tile.getEnchLevel(1);
                    else tile.getEnchLevel(0);
                } else {
                    if(handItem.getItem() == Items.BOOK ||
                            handItem.getItem() instanceof DiggerItem ||
                            handItem.getItem() instanceof SwordItem ||
                            handItem.getItem() instanceof BowItem ||
                            handItem.getItem() instanceof CrossbowItem ||
                            handItem.getItem() instanceof TridentItem) {
                        boolean isTier2 = !tile.handler.getStackInSlot(0).isEmpty()
                                && !tile.handler.getStackInSlot(1).isEmpty();
                        if(isTier2)
                            return tile.enchantBook(player, handItem, 1);
                        else {
                            if(!tile.handler.getStackInSlot(0).isEmpty() ||
                                    !tile.handler.getStackInSlot(0).isEmpty())
                                return tile.enchantBook(player, handItem,  0);
                        }
                    } else
                        return tile.enchantItem(player, handItem);
                }
            }
        } else {
            WordMachineBlockEntity tile = (WordMachineBlockEntity)world.getBlockEntity(pos);
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 0.95D;
            double z = pos.getZ() + 0.5D;
            player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
            if(!tile.handler.getStackInSlot(0).isEmpty() || !tile.handler.getStackInSlot(1).isEmpty())
                world.addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, 0.0D, 0.025D,
                        0.0D);
        }
        return InteractionResult.PASS;
    }
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if(rand.nextInt(9) < 3) return;
        WordMachineBlockEntity te;
        if(world.getBlockEntity(pos) instanceof WordMachineBlockEntity)
            te = (WordMachineBlockEntity) world.getBlockEntity(pos);
        else return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;
        double randomA = rand.nextDouble() - 0.4;
        double randomB = rand.nextDouble() - 0.3;
        if((!te.handler.getStackInSlot(0).isEmpty() || !te.handler.getStackInSlot(1).isEmpty()) &&
                !(!te.handler.getStackInSlot(0).isEmpty() && !te.handler.getStackInSlot(1).isEmpty()))
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        if(!te.handler.getStackInSlot(1).isEmpty() && !te.handler.getStackInSlot(0).isEmpty())
            world.addParticle(ParticleTypes.SOUL,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        world.addParticle(ParticleTypes.ENCHANT, x + randomA, y + randomA-randomB,
                z + randomB, 0.0D, 0.015D, 0.0D);
    }
}
