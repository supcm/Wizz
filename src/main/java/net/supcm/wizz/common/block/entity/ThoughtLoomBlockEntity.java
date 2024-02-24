package net.supcm.wizz.common.block.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.data.recipes.ConceptRecipe;
import net.supcm.wizz.data.recipes.Recipes;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ThoughtLoomBlockEntity extends BlockEntity {
    public ThoughtLoomBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntities.THOUGHT_LOOM.get(), p_155229_, p_155230_);
    }
    public final ItemStackHandler handler = new ItemStackHandler(4) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);}
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot != 0 || stack.getItem() instanceof Items.UnstableGlyphItem;
        }
        @Override public int getSlotLimit(int slot) { return slot == 0 ? 1 : super.getSlotLimit(slot); }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
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
    public InteractionResult createConception(Player player, ItemStack handItem){
        if(!handler.getStackInSlot(0).isEmpty() && (
                !handler.getStackInSlot(1).isEmpty() ||
                        !handler.getStackInSlot(2).isEmpty() ||
                        !handler.getStackInSlot(3).isEmpty())) {
            List<RecipeHolder<ConceptRecipe>> recipes = level.getRecipeManager()
                    .getAllRecipesFor(Recipes.CONCEPT.get());
            for(RecipeHolder<ConceptRecipe> holder : recipes) {
                ConceptRecipe recipe = holder.value();
                if(recipe.level() <= player.experienceLevel ||
                        player.isCreative()) {
                    int lvl = recipe.level();
                    ItemStack output = recipe.getResultItem(level.registryAccess());
                    NonNullList<Ingredient> inputs = recipe.getIngredients();
                    NonNullList<Item> inv = NonNullList.withSize(handler.getSlots(), net.minecraft.world.item.Items.AIR);
                    for(int i = 0; i < handler.getSlots(); i++)
                        inv.set(i, handler.getStackInSlot(i).getItem());
                    if(inputs.get(0).test(handler.getStackInSlot(0)) &&
                            Arrays.stream(inputs.get(1).getItems()).anyMatch(i -> inv.contains(i.getItem())) &&
                            Arrays.stream(inputs.get(2).getItems()).anyMatch(i -> inv.contains(i.getItem()))&&
                            Arrays.stream(inputs.get(3).getItems()).anyMatch(i -> inv.contains(i.getItem()))) {
                        if(!player.isCreative() && lvl != -1) {
                            player.onEnchantmentPerformed(output, lvl);
                            handItem.shrink(1);
                        }
                        level.addFreshEntity(new ItemEntity(level,
                                worldPosition.getX() + 0.5,
                                worldPosition.getY() + 1.25,
                                worldPosition.getZ() + 0.5,
                                output));
                        if(level.getRandom().nextInt(4) == 0) handler.getStackInSlot(0).shrink(1);
                        handler.getStackInSlot(1).shrink(1);
                        handler.getStackInSlot(2).shrink(1);
                        handler.getStackInSlot(3).shrink(1);
                        player.awardStat(Stats.ENCHANT_ITEM);
                        if (player instanceof ServerPlayer)
                            CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, output, 1 | 2);
                        level.playSound(null, worldPosition, SoundEvents.TRIPWIRE_DETACH, SoundSource.BLOCKS,
                                1.0f, 1.0f);
                        setChanged();
                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
                        return InteractionResult.SUCCESS;
                    }
                } else
                    player.displayClientMessage(Component.translatable("enchanting.notenoughxp", 0),
                            true);
            }
        }
        return InteractionResult.PASS;
    }
}
