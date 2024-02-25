package net.supcm.wizz.common.block.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.wizz.WizzModConfig;
import net.supcm.wizz.common.handler.EnchantmentsHandler;
import net.supcm.wizz.common.item.CodexItem;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.data.recipes.EnchantingRecipe;
import net.supcm.wizz.data.recipes.Recipes;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class WordMachineBlockEntity extends BlockEntity {
    public WordMachineBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.WORD_MACHINE.get(), pos, state);
    }
    public final ItemStackHandler handler = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);}
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() instanceof Items.GlyphItem;
        }
        @Override public int getSlotLimit(int slot) { return 4; }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    public int enchLevel;
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
        enchLevel = tag.getInt("EnchantmentLevel");
    }
    @Override public void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", handler.serializeNBT());
        tag.putInt("EnchantmentLevel", enchLevel);
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
    public void getEnchLevel(int tier) {
        Enchantment ench = null;
        if(tier == 0) ench = getEnchantment();
        else if(tier == 1)
            if(getEnchantmentT2() != null) ench = getEnchantmentT2();
        if(ench == null) {
            enchLevel = -1;
            return;
        }
        int count = Math.max(handler.getStackInSlot(0).getCount(),
                handler.getStackInSlot(1).getCount());
        if(count > ench.getMaxLevel())
            count = ench.getMaxLevel();
        enchLevel = (WizzModConfig.ENCHANTING_MULT.get() / ench.getMaxLevel())*count;
    }
    public Enchantment getEnchantment() {
        Enchantment ench = null;
        ResourceLocation rl = Items.getResourceLocation(handler.getStackInSlot(0)
                .getItem());
        if(EnchantmentsHandler.T1_MAP.keySet().contains(rl.getPath()))
            ench = EnchantmentsHandler.T1_MAP.get(rl.getPath());
        return ench;
    }
    public Enchantment getEnchantmentT2() {
        Enchantment ench = null;
        ResourceLocation rl = Items.getResourceLocation(handler.getStackInSlot(0)
                .getItem());
        ResourceLocation rl1 = Items.getResourceLocation(handler.getStackInSlot(1)
                .getItem());
        if(EnchantmentsHandler.T2_MAP.keySet().contains(rl.getPath() + "_" + rl1.getPath()))
            ench = EnchantmentsHandler.T2_MAP.get(rl.getPath() + "_" + rl1.getPath());
        return ench;
    }
    public InteractionResult enchantBook(Player player, ItemStack handItem, int tier) {
        Enchantment ench = null;
        if(tier == 1)
            ench = getEnchantmentT2();
        else if(tier == 0)
            ench = getEnchantment();
        if(ench == null || enchLevel == -1) return InteractionResult.PASS;
        int count = Math.max(handler.getStackInSlot(0).getCount(),
                handler.getStackInSlot(1).getCount());
        if(count > ench.getMaxLevel())
            count = ench.getMaxLevel();
        if (player.experienceLevel < enchLevel && !player.isCreative()){
            player.displayClientMessage(Component.translatable("enchanting.notenoughxp", 0), true);
            return InteractionResult.PASS;
        }
        if(handItem.getItem() == net.minecraft.world.item.Items.BOOK){
            ItemStack stack = new ItemStack(net.minecraft.world.item.Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(ench, count));
            player.addItem(stack);
        } else {
            Map<Enchantment, Integer> data = EnchantmentHelper.getEnchantments(handItem);
            data.putIfAbsent(ench, count);
            EnchantmentHelper.setEnchantments(data, handItem);
        }
        if (!player.isCreative()) {
            player.onEnchantmentPerformed(handItem, enchLevel);
            if(handItem.getMaxStackSize() > 1)
                handItem.shrink(1);
        }
        level.explode(player, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 1.25D,
                getBlockPos().getZ() + 0.5D, 0.5f, Level.ExplosionInteraction.NONE);
        for(ItemStack codex : player.getInventory().items) {
            if(codex.getItem() instanceof CodexItem) {
                StringTag str = StringTag.valueOf(CodexItem.getGlyphsFor(ench)
                        + "'" + EnchantmentsHandler.getEnchantmentId(ench));
                List<Tag> tag = codex.getOrCreateTag().getList("Revealed", 8);
                if(!tag.contains(str))
                    tag.add(str);
            }
        }
        player.awardStat(Stats.ENCHANT_ITEM);
        if (player instanceof ServerPlayer)
            CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, handItem, 1 | 2);
        setChanged();
        return InteractionResult.SUCCESS;
    }
    public InteractionResult enchantItem(Player player, ItemStack handItem) {
        if(!handler.getStackInSlot(0).isEmpty()){
            List<EnchantingRecipe> recipeList = level.getRecipeManager()
                    .getAllRecipesFor(Recipes.ENCHANTING.get());
            for (EnchantingRecipe recipe : recipeList) {
                if (recipe.tier() <= 1 &&
                        recipe.input().test(handItem)) {
                    ItemStack output = recipe.getResultItem(level.registryAccess());
                    int thisLevel = recipe.level();
                    if (player.experienceLevel < thisLevel && !player.isCreative()){
                        player.displayClientMessage(Component.translatable("enchanting.notenoughxp", 0), true);
                        return InteractionResult.PASS;
                    }
                    if (!player.isCreative()) {
                        player.onEnchantmentPerformed(output, thisLevel);
                        handItem.shrink(1);
                    }
                    player.addItem(output);
                    level.explode(player, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 1.25D,
                            getBlockPos().getZ() + 0.5D, 0.5f, Level.ExplosionInteraction.NONE);
                    player.awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayer)
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, output, 1 | 2);
                    setChanged();
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
