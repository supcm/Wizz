package net.supcm.wizz.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.supcm.wizz.common.block.entity.WizzTransBlockEntity;
import org.jetbrains.annotations.Nullable;

public class WizzTransBlock extends Block /*implements EntityBlock*/ {
    public WizzTransBlock() {
        super(Properties.copy(Blocks.DEEPSLATE));
    }

    /*@Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WizzTransBlockEntity(pos, state);
    }
    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_,
                                                                  BlockEntityType<T> p_153214_) {
        return (level, state, pos, blockentity) -> ((WizzTransBlockEntity)blockentity).tick();
    }

    @Override
    public void onRemove(BlockState p_60515_, Level level, BlockPos pos, BlockState p_60518_, boolean p_60519_) {
        if(level.getBlockEntity(pos) instanceof WizzTransBlockEntity blockEntity) {
            if(blockEntity.vector != null) {
                if(level.getBlockEntity(new BlockPos((int) (pos.getX() + blockEntity.vector.x),
                        pos.getY(), (int) (pos.getZ() + blockEntity.vector.z))) instanceof WizzTransBlockEntity blockEntity1) {
                    blockEntity1.vector = null;
                }
                blockEntity.vector = null;
            }

        }
        super.onRemove(p_60515_, level, pos, p_60518_, p_60519_);
    }*/
}
