package net.supcm.wizz.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.wizz.common.item.Items;

import javax.annotation.Nonnull;

public class MatrixBlockEntity extends BlockEntity {
    public MatrixBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntities.MATRIX.get(), p_155229_, p_155230_);
    }
    public final ItemStackHandler handler = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);}
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot == 0 ? stack.getItem() == Items.PLATE.get() :
                    (stack.getItem() instanceof Items.GlyphItem ||
                            stack.getItem() instanceof Items.UnstableGlyphItem);
        }
        @Override public int getSlotLimit(int slot) { return slot == 0 ? 64 : 1; }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    public int tick;
    public boolean doCraft;
    public int renderTick;
    public boolean doRenderCrystal;
    @Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
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
        tick = tag.getInt("Tick");
        doCraft = tag.getBoolean("DoCraft");
    }
    @Override public void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", handler.serializeNBT());
        tag.putInt("Tick", tick);
        tag.putBoolean("DoCraft", doCraft);
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
    public void setDoCraft(boolean craft) {
        doCraft = craft;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
    }
    public void clientTick() {
        if(doCraft) {
            renderTick++;
            doRenderCrystal = true;
        } else {
            doRenderCrystal = false;
            renderTick = 0;
        }
    }
    public void tick() {
        if(doCraft) {
            tick++;
            if(tick >= 60) {
                handler.getStackInSlot(0).shrink(1);
                level.explode(null, getBlockPos().getX() + 0.5D,
                        getBlockPos().getY() + 1.25D,
                        getBlockPos().getZ() + 0.5D, 0.1f, Level.ExplosionInteraction.NONE);
                ItemStack stack = new ItemStack(Items.CONCEPT_BASE.get(), 4);
                if(!handler.getStackInSlot(1).isEmpty())
                    stack = handler.getStackInSlot(1).copy();
                level.addFreshEntity(new ItemEntity(level, getBlockPos().getX()+0.5,
                        getBlockPos().getY()+1.35D,
                        getBlockPos().getZ()+0.5, stack));
                tick = 0;
                setDoCraft(false);
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
            }
        }
    }
}
