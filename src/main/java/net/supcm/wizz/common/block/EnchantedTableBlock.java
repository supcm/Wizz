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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.supcm.wizz.common.block.entity.EnchantedTableBlockEntity;
import net.supcm.wizz.common.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class EnchantedTableBlock extends Block implements EntityBlock {
    public static final VoxelShape SHAPE = Shapes.create(new AABB(0.0D, 0.0D,
            0.0D, 1.0D, 0.565D, 1.0D));
    public EnchantedTableBlock() {
        super(Properties.of().strength(7f).requiresCorrectToolForDrops());
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantedTableBlockEntity(pos, state);
    }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public void onRemove(BlockState state, Level world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof EnchantedTableBlockEntity te) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof EnchantedTableBlockEntity tile) {
            ItemStack handItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(hand == InteractionHand.MAIN_HAND) {
                if(handItem.isEmpty() || handItem.getItem() instanceof Items.GlyphItem) {
                    tile.insertOrExtractItem(player, 0);
                    tile.getEnchLevel();
                    world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1f, 1f);
                } else {
                    if(!tile.handler.getStackInSlot(0).isEmpty()){
                        if (handItem.getItem() == net.minecraft.world.item.Items.BOOK ||
                                handItem.getItem() instanceof DiggerItem ||
                                handItem.getItem() instanceof SwordItem ||
                                handItem.getItem() instanceof BowItem ||
                                handItem.getItem() instanceof CrossbowItem ||
                                handItem.getItem() instanceof TridentItem) {
                            return tile.enchantBook(player, handItem);
                        } else {
                            return tile.enchantItem(player, handItem);
                        }
                    }
                }
            }
        } else {
            EnchantedTableBlockEntity tile = (EnchantedTableBlockEntity)world.getBlockEntity(pos);
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 0.95D;
            double z = pos.getZ() + 0.5D;
            player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
            if (!tile.handler.getStackInSlot(0).isEmpty())
                world.addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, 0.0D, 0.025D, 0.0D);
        }
        return InteractionResult.PASS;
    }

    @Override public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if(rand.nextInt(9) < 3) return;
        EnchantedTableBlockEntity te;
        if(world.getBlockEntity(pos) instanceof EnchantedTableBlockEntity)
            te = (EnchantedTableBlockEntity) world.getBlockEntity(pos);
        else return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.75D;
        double z = pos.getZ() + 0.5D;
        double randomA = rand.nextDouble() - 0.4;
        double randomB = rand.nextDouble() - 0.3;
        if(!te.handler.getStackInSlot(0).isEmpty()) {
            world.addParticle(ParticleTypes.SOUL,
                    x + randomA, y + 0.15 + randomA - randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        }
        world.addParticle(ParticleTypes.ENCHANT, x + randomA, y + randomA-randomB,
                z + randomB, 0.0D, 0.015D, 0.0D);
    }
}
