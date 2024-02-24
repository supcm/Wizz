package net.supcm.wizz.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.supcm.wizz.common.block.entity.AlchemyCauldronBlockEntity;
import net.supcm.wizz.common.block.entity.EnchantingStationBlockEntity;
import net.supcm.wizz.common.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class AlchemyCauldronBlock extends Block implements EntityBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(1, 0, 1, 15, 2, 15),
            Block.box(1, 16, 15, 15, 18, 16),
            Block.box(1, 16, 0, 15, 18, 1),
            Block.box(15, 16, 0, 16, 18, 16),
            Block.box(0, 16, 0, 1, 18, 16),
            Block.box(1, 14, 1, 2, 16, 15),
            Block.box(14, 14, 1, 15, 16, 15),
            Block.box(2, 14, 1, 14, 16, 2),
            Block.box(2, 14, 14, 14, 16, 15),
            Block.box(1, 2, 15, 15, 14, 16),
            Block.box(0, 2, 0, 1, 14, 16),
            Block.box(15, 2, 0, 16, 14, 16),
            Block.box(1, 2, 0, 15, 14, 1)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final BooleanProperty WATER = BooleanProperty.create("water");
    public AlchemyCauldronBlock() {
        super(Properties.of().strength(2, 2));
        registerDefaultState(defaultBlockState().setValue(WATER, false));
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
    /*@Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }*/
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemyCauldronBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (level, pos, state, blockEntity) -> ((AlchemyCauldronBlockEntity)blockEntity).tick(state, pos);
    }

    @Override
    public void onRemove(BlockState nextState, Level level, BlockPos pos, BlockState state, boolean flag) {
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof AlchemyCauldronBlockEntity te) {
            if(te.isProcessing()) {
                ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new ItemStack(Items.ALCHEMY_WASTE.get()));
                level.addFreshEntity(item);
            }
        } else {
            level.addParticle(ParticleTypes.SOUL, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    0, 0.5, 0);
        }
        super.onRemove(nextState, level, pos, state, flag);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if(!level.isClientSide && state.getValue(WATER) &&
                level.getBlockEntity(pos) instanceof AlchemyCauldronBlockEntity alchemy) {
            if(alchemy.isBoiling()) {
                if(entity instanceof ItemEntity item) {
                    alchemy.insertItem(item.getItem());
                    item.kill();
                } else if(entity instanceof LivingEntity living)
                    living.hurt(living.damageSources().cactus(), 0.5f);
            }
        }
        super.entityInside(state, level, pos, entity);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATER.value(false).property());
    }
    @Nullable @Override public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(WATER, false);
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(!level.isClientSide) {
            AlchemyCauldronBlockEntity tile = (AlchemyCauldronBlockEntity) level.getBlockEntity(pos);
            if(hand == InteractionHand.MAIN_HAND) {
                ItemStack handItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                if(handItem.getItem() == net.minecraft.world.item.Items.WATER_BUCKET && !level.getBlockState(pos).getValue(WATER)) {
                    level.setBlockAndUpdate(pos, Blocks.ALCHEMY_CAULDRON.get().defaultBlockState().setValue(WATER, true));
                    if(!player.isCreative())
                        handItem.shrink(1);
                    player.addItem(new ItemStack(net.minecraft.world.item.Items.BUCKET));
                }
                else {
                    if (handItem.isEmpty()) {
                        tile.extractItem(player, pos);
                    } else if (handItem.getItem() instanceof Items.GlyphItem) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, tile.handler.insertItem(0, handItem, false));
                    } else if (tile.handler.isItemValid(1, handItem)) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, tile.insertItem(handItem));
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        AlchemyCauldronBlockEntity tile = (AlchemyCauldronBlockEntity) level.getBlockEntity(pos);
        double x = pos.getX() + random.nextDouble() + 0.1d;
        double y = pos.getY() + 1d;
        double z = pos.getZ() + random.nextDouble() + 0.1d;
        x -= x >= pos.getX() + 0.9d ? 0.15d : 0;
        z -= z >= pos.getX() + 0.9d ? 0.15d : 0;
        if(tile.isBoiling()) {
            level.addParticle(ParticleTypes.BUBBLE,
                    x, y, z, 0d, 0.1d, 0d);
        }
        super.animateTick(state, level, pos, random);
    }
}
