package net.supcm.wizz.common.block.entity;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Map;

public class WordEraserBlockEntity extends BlockEntity {
    public WordEraserBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntities.WORD_ERASER.get(), p_155229_, p_155230_);
    }
    public final ItemStackHandler handler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
            currentEnchantment = 0;
            currentEnchantmentName = "";
            changeCurrentEnchantment();
        }
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.isEnchanted() && !(stack.getItem() instanceof net.supcm.wizz.common.item.Items.UnstableGlyphItem)
                    && stack.getItem() != Items.ENCHANTED_BOOK;
        }
        @Override public int getSlotLimit(int slot) { return 1; }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    public String currentEnchantmentName = "";
    Map<Enchantment, Integer> enchs = Maps.newHashMap();
    public int currentEnchantment = 0;
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
        currentEnchantmentName = tag.getString("EnchantmentName");
        currentEnchantment = tag.getInt("Enchantment");
    }
    @Override public void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", handler.serializeNBT());
        tag.putString("EnchantmentName", currentEnchantmentName);
        tag.putInt("Enchantment", currentEnchantment);
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
    public void tick() {
        if(!handler.getStackInSlot(0).isEmpty() && level.getGameTime() % 50 == 0)
            changeCurrentEnchantment();
    }
    void changeCurrentEnchantment() {
        enchs = EnchantmentHelper.getEnchantments(handler.getStackInSlot(0));
        if (!enchs.isEmpty()) {
            if(currentEnchantment < enchs.size() - 1)
                currentEnchantment++;
            else currentEnchantment = 0;
            currentEnchantmentName = ((Enchantment) enchs.keySet().toArray()[currentEnchantment]).getDescriptionId();
        } else {
            currentEnchantmentName = "";
            currentEnchantment = 0;
        }
    }
    public void proceedErasing() {
        if(!enchs.isEmpty()) {
            ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
            int i = 0;
            for(Map.Entry<Enchantment, Integer> entry : enchs.entrySet()) {
                if(i == currentEnchantment) {
                    EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(entry.getKey(),
                            entry.getValue()));
                    enchs.remove(entry.getKey());
                    EnchantmentHelper.setEnchantments(enchs, handler.getStackInSlot(0));
                    if(currentEnchantment > 0) currentEnchantment--;
                    changeCurrentEnchantment();
                    setChanged();
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
                    level.addFreshEntity(new ItemEntity(level,
                            worldPosition.getX() + 0.5d, worldPosition.getY() + 1.25d,
                            worldPosition.getZ() + 0.5d,
                            stack));
                    break;
                } else
                    i++;
            }
        }
    }
}
