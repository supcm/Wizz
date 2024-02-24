package net.supcm.wizz.common.block.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.wizz.WizzModConfig;
import net.supcm.wizz.common.handler.EnchantmentsHandler;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.data.recipes.EnchantingRecipe;
import net.supcm.wizz.data.recipes.Recipes;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class EnchantedTableBlockEntity extends BlockEntity {
    public final ItemStackHandler handler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);}
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() instanceof Items.GlyphItem;
        }
        @Override public int getSlotLimit(int slot) { return 2; }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    public int enchLevel;
    public EnchantedTableBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntities.ENCHANTED_TABLE.get(), p_155229_, p_155230_);
    }
    @Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return cap == ForgeCapabilities.ITEM_HANDLER ?
                inventory.cast() : super.getCapability(cap);
    }

    @Override
    public void invalidateCaps() {
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

    public void getEnchLevel() {
        Enchantment ench = getEnchantment();
        if(ench == null) return;
        int count = handler.getStackInSlot(0).getCount();
        if(count > ench.getMaxLevel())
            count = ench.getMaxLevel();
        enchLevel = (WizzModConfig.ENCHANTING_MULT.get() /ench.getMaxLevel())*count;
    }
    public Enchantment getEnchantment() {
        Enchantment ench = null;
        ResourceLocation rl = Items.getResourceLocation(handler.getStackInSlot(0)
                .getItem());
        if(EnchantmentsHandler.T1_MAP.keySet().contains(rl.getPath()))
            ench = EnchantmentsHandler.T1_MAP.get(rl.getPath());
        return ench;
    }
    public InteractionResult enchantBook(Player player, ItemStack handItem) {
        Enchantment ench = getEnchantment();
        if(ench == null) return InteractionResult.PASS;
        int count = handler.getStackInSlot(0).getCount();
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
        player.awardStat(Stats.ENCHANT_ITEM);
        if (player instanceof ServerPlayer)
            CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer) player, handItem, 1 | 2);
        setChanged();
        return InteractionResult.CONSUME;
    }
    public InteractionResult enchantItem(Player player, ItemStack handItem) {
        if(!handler.getStackInSlot(0).isEmpty()){
            List<RecipeHolder<EnchantingRecipe>> recipeList = level.getRecipeManager()
                    .getAllRecipesFor(Recipes.ENCHANTING.get());
            for (RecipeHolder<EnchantingRecipe> recipeHolder : recipeList) {
                EnchantingRecipe recipe = recipeHolder.value();
                if (recipe.tier() <= 0 &&
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
