package net.supcm.wizz.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WizzTransBlockEntity /*extends BlockEntity*/ {
    public Vec3 vector = null;

    /*public WizzTransBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.WIZZ_TRANS.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        CompoundTag vec = tag.getCompound("Vector");
        double x = vec.getDouble("X");
        double y = vec.getDouble("Y");
        double z = vec.getDouble("Z");
        vector = new Vec3(x, y, z);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        CompoundTag vec = new CompoundTag();
        if(vector != null) {
            vec.putDouble("X", vector.x);
            vec.putDouble("Y", vector.y);
            vec.putDouble("Z", vector.z);
        }
        tag.put("Vector", vec);
        super.saveAdditional(tag);
    }

    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }
    @Override public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }
    @Override public void handleUpdateTag(CompoundTag tag) { load(tag); }

    public void tick() {
        if(vector == null) {
            start: for (int x = -5; x <= 5; x++) {
                for (int z = -5; z <= 5; z++) {
                    if(x == 0 && z == 0 )
                        continue;
                    if(level.getBlockEntity(new BlockPos(getBlockPos().getX() + x, getBlockPos().getY(),
                            getBlockPos().getZ() + z)) instanceof WizzTransBlockEntity blockEntity) {
                        vector = new Vec3(x, 0, z);
                        blockEntity.vector = new Vec3(-x, 0, -z);
                        break start;
                    }
                }
            }
        }
    }*/
}
