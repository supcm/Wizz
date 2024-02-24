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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.wizz.common.item.Items;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ReassessmentPillarBlockEntity extends BlockEntity {
    public ReassessmentTableBlockEntity tile = null;
    public List<Integer> concepts = createEmptyConcepts();
    public final ItemStackHandler handler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) {
            if(tile != null) {
                tile.pillars.replace(ReassessmentPillarBlockEntity.this.getBlockPos(), handler.getStackInSlot(slot));
                tile.updateRecipe();
            }
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
        }
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() instanceof Items.ConceptItem;
        }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    @Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? inventory.cast() : super.getCapability(cap);
    }
    @Override public void setChanged() {
        concepts = tile != null ? tile.concepts : createEmptyConcepts();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
        super.setChanged();
    }
    public ReassessmentPillarBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntities.REASSESSMENT_PILLAR.get(), p_155229_, p_155230_);
    }
    @Override public void invalidateCaps() {
        super.invalidateCaps();
        inventory.invalidate();
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag);
        handler.deserializeNBT(tag.getCompound("Inventory"));
        List<Integer> list = new ArrayList<>();
        for(int i : tag.getIntArray("Concepts"))
            list.add(i);
        concepts = list;
    }
    @Override public void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", handler.serializeNBT());
        tag.putIntArray("Concepts", concepts);
        super.saveAdditional(tag);
    }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) { load(pkt.getTag());}
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
    void setLinkedTable(ReassessmentTableBlockEntity tile){
        this.tile = tile;
        setChanged();
    }
    List<Integer> createEmptyConcepts() {
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < 6; i++)
            list.add(0);
        return list;
    }
}
