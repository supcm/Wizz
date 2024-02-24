package net.supcm.wizz.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.wizz.common.item.Items;

import javax.annotation.Nonnull;

public class MortarBlockEntity extends BlockEntity {
    public final ItemStackHandler handler = new ItemStackHandler(5) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);}
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() != Items.PESTLE.get();
        }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    public MortarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MORTAR.get(), pos, state);
    }
    @Nonnull
    @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return cap == ForgeCapabilities.ITEM_HANDLER ?
                inventory.cast() : super.getCapability(cap);
    }
    @Override public void invalidateCaps() {
        super.invalidateCaps();
        inventory.invalidate();
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag);
        handler.deserializeNBT(tag.getCompound("Inventory"));
    }
    @Override public void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", handler.serializeNBT());
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
    public void insertOrExtractItem(Player player, int slot) {
        if(!(handler.getStackInSlot(slot).isEmpty() || player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) &&
                (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == handler.getStackInSlot(slot).getItem())) {
            if(player.getItemInHand(InteractionHand.MAIN_HAND).getCount() + handler.getStackInSlot(slot).getCount()
                    <= player.getItemInHand(InteractionHand.MAIN_HAND).getMaxStackSize()) {
                player.getItemInHand(InteractionHand.MAIN_HAND).grow(handler.getStackInSlot(slot).getCount());
                handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false);
            } else {
                if(player.getInventory().getFreeSlot() != -1)
                    player.addItem(handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false));
                else
                    level.addFreshEntity(new ItemEntity(level,
                            player.blockPosition().getX() + 0.5,
                            player.blockPosition().getY() + 0.5,
                            player.blockPosition().getZ() + 0.5,
                            handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false)));
            }
        } else if(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()){
            if(player.getInventory().getFreeSlot() != -1)
                player.addItem(handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false));
            else
                level.addFreshEntity(new ItemEntity(level,
                        player.blockPosition().getX() + 0.5,
                        player.blockPosition().getY() + 0.5,
                        player.blockPosition().getZ() + 0.5,
                        handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false)));
        } else {
            player.setItemInHand(InteractionHand.MAIN_HAND, handler.insertItem(slot,
                    player.getItemInHand(InteractionHand.MAIN_HAND), false));
        }
    }
}
