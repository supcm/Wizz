package net.supcm.wizz.common.block.entity;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantingStationBlockEntity extends BlockEntity {
    public EnchantingStationBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntities.ENCHANTING_STATION.get(), p_155229_, p_155230_);
    }
    public final ItemStackHandler handler = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);}
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return true; }
        @Override public int getSlotLimit(int slot) { return slot == 0 ? 1 : 5; }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    public int tick;
    public boolean doCraft = false;
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
    }
    @Override public void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", handler.serializeNBT());
        tag.putInt("Tick", tick);
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
        if((!handler.getStackInSlot(0).isEmpty() && !handler.getStackInSlot(1).isEmpty()) &&
                ((handler.getStackInSlot(0).getItem().isValidRepairItem(handler.getStackInSlot(0),
                        handler.getStackInSlot(1)) && handler.getStackInSlot(0).isDamageableItem()
                        && handler.getStackInSlot(0).isDamaged()) ||
                        (handler.getStackInSlot(1).getItem() == Items.ENCHANTED_BOOK &&
                                !EnchantedBookItem.getEnchantments(handler.getStackInSlot(1)).isEmpty())
                        || handler.getStackInSlot(1).getItem() == net.supcm.wizz.common.item.Items.FIR.get() &&
                        handler.getStackInSlot(0).isEnchanted())) {
            doCraft = true;
        }
        if(doCraft) {
            tick++;
            if(tick >= 100) {
                if(handler.getStackInSlot(1).getItem() == Items.ENCHANTED_BOOK) {
                    if(handler.getStackInSlot(0).isEnchanted()) {
                        Map<Enchantment, Integer> data = EnchantmentHelper.getEnchantments(handler.getStackInSlot(0));
                        for(int i = 0; i < EnchantmentHelper.getEnchantments(handler.getStackInSlot(1)).size(); i++) {
                            Enchantment ench = (Enchantment)EnchantmentHelper.getEnchantments(handler.getStackInSlot(1))
                                    .keySet().toArray()[i];
                            if(data.containsKey(ench)){
                                data.replace(ench,
                                        Math.max(data.get(ench),
                                                EnchantmentHelper.getEnchantments(handler.getStackInSlot(1)).get(ench)));
                            } else {
                                data.put(ench, EnchantmentHelper.getEnchantments(handler.getStackInSlot(1)).get(ench));
                            }
                        }
                        EnchantmentHelper.setEnchantments
                                (data, handler.getStackInSlot(0));
                    } else
                        EnchantmentHelper.setEnchantments
                                (EnchantmentHelper.getEnchantments(handler.getStackInSlot(1)), handler.getStackInSlot(0));
                } else if (handler.getStackInSlot(1).getItem() == net.supcm.wizz.common.item.Items.FIR.get()) {
                    Map<Enchantment, Integer> data = EnchantmentHelper.getEnchantments(handler.getStackInSlot(0));
                    List<Map.Entry<Enchantment, Integer>> list = new ArrayList<>(data.entrySet());
                    boolean doTry = true;
                    int i = 0;
                    while(doTry) {
                        int k = level.getRandom().nextInt(list.size());
                        if(list.get(k).getKey().getMaxLevel() != 1 && list.get(k).getValue() != 20) {
                            data.replace(list.get(k).getKey(), list.get(k).getValue(), 20);
                            doTry = false;
                        } else if(i == list.size()){
                            doCraft = false;
                            getLevel().addFreshEntity(new ItemEntity(getLevel(),
                                    getBlockPos().getX()+0.5d,
                                    getBlockPos().getY()+1.35d,
                                    getBlockPos().getZ()+0.5d,
                                    handler.getStackInSlot(1).copy()));
                            handler.getStackInSlot(1).shrink(1);
                            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
                            tick = 0;
                            return;
                        }
                        i++;
                    }
                    EnchantmentHelper.setEnchantments(data, handler.getStackInSlot(0));
                    /*handler.getStackInSlot(0).setHoverName(new StringTextComponent(
                            handler.getStackInSlot(0).getHoverName().getString())
                            .withStyle(TextFormatting.DARK_RED));*/
                } else {
                    handler.getStackInSlot(0).setDamageValue(-
                            (((handler.getStackInSlot(0).getMaxDamage()/5)*handler.getStackInSlot(1).getCount())-
                                    handler.getStackInSlot(0).getDamageValue()));
                }
                doCraft = false;
                handler.getStackInSlot(1).shrink(1);
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
                tick = 0;
            }
        }
    }
}
