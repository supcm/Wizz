package net.supcm.wizz.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;
import net.supcm.wizz.common.block.entity.MortarBlockEntity;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.common.network.PacketHandler;
import net.supcm.wizz.common.network.packets.MortarScreenPacket;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class MortarBlock extends Block implements EntityBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(5, 0, 5, 11, 1, 11),
            Block.box(4, 3, 3, 12, 5, 4),
            Block.box(11, 1, 5, 12, 3, 11),
            Block.box(4, 1, 5, 5, 3, 11),
            Block.box(4, 1, 11, 12, 3, 12),
            Block.box(4, 1, 4, 12, 3, 5),
            Block.box(4, 3, 12, 12, 5, 13),
            Block.box(3, 3, 4, 4, 5, 12),
            Block.box(12, 3, 4, 13, 5, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();;
    public MortarBlock() {
        super(Properties.of().strength(1.0f));
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MortarBlockEntity(pos, state);
    }
    @Override public void onRemove(BlockState new_state, Level level, BlockPos pos, BlockState state, boolean flag) {
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof MortarBlockEntity te)
            for(int i = 0; i < te.handler.getSlots(); i++)
                level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(),
                        te.handler.getStackInSlot(i)));
        super.onRemove(new_state, level, pos, state, flag);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if(!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            ItemStack handItem = player.getItemInHand(hand);
            if(handItem.getItem() != Items.PESTLE.get()) {
                MortarBlockEntity tile = (MortarBlockEntity) level.getBlockEntity(pos);
                if(!player.isCrouching()) {
                    if(tile.handler.getStackInSlot(0).isEmpty())
                        tile.insertOrExtractItem(player, 0);
                    else if(tile.handler.getStackInSlot(1).isEmpty())
                        tile.insertOrExtractItem(player, 1);
                    else if(tile.handler.getStackInSlot(2).isEmpty())
                        tile.insertOrExtractItem(player, 2);
                    else if(tile.handler.getStackInSlot(3).isEmpty())
                        tile.insertOrExtractItem(player, 3);
                    else
                        tile.insertOrExtractItem(player, 4);
                    return InteractionResult.CONSUME;
                } else {
                    if(!tile.handler.getStackInSlot(4).isEmpty())
                        tile.insertOrExtractItem(player, 4);
                    else if(!tile.handler.getStackInSlot(3).isEmpty())
                        tile.insertOrExtractItem(player, 3);
                    else if(!tile.handler.getStackInSlot(2).isEmpty())
                        tile.insertOrExtractItem(player, 2);
                    else if(!tile.handler.getStackInSlot(1).isEmpty())
                        tile.insertOrExtractItem(player, 1);
                    else
                        tile.insertOrExtractItem(player, 0);
                    return InteractionResult.SUCCESS;
                }
            } else {
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new MortarScreenPacket(pos));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
