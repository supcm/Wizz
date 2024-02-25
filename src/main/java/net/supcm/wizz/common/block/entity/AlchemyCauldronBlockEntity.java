package net.supcm.wizz.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.block.AlchemyCauldronBlock;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.data.recipes.AlchemyRecipe;
import net.supcm.wizz.data.recipes.Recipes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AlchemyCauldronBlockEntity extends BlockEntity {
    private List<AlchemyRecipe.Step> steps = new ArrayList<>();

    private boolean isProcessing = false;
    private boolean isBoiling = false;

    private int seconds = 0;

    public final ItemStackHandler handler = new ItemStackHandler(6) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);}
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if(slot == 0)
                return stack.getItem() instanceof Items.GlyphItem;
            else
                return stack.getItem() != Items.ALCHEMY_WASTE.get() && stack.getItem() != Items.WIZZ.get();
        }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    public AlchemyCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.ALCHEMY_CAULDRON.get(), pos, state);
    }
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
        isBoiling = tag.getBoolean("IsBoiling");
        isProcessing = tag.getBoolean("IsProcessing");
        handler.deserializeNBT(tag.getCompound("Inventory"));
        List<AlchemyRecipe.Step> steps = new ArrayList<>();
        for(Tag step : tag.getList("Steps", 10)) {
            steps.add(AlchemyRecipe.Step.load((CompoundTag) step));
        }
        this.steps = steps;
        seconds = tag.getInt("Seconds");
    }
    @Override public void saveAdditional(CompoundTag tag) {
        tag.putBoolean("IsBoiling", isBoiling);
        tag.putBoolean("IsProcessing", isProcessing);
        tag.put("Inventory", handler.serializeNBT());
        ListTag tags = new ListTag();
        int i = 0;
        for(AlchemyRecipe.Step step : steps)
            tags.addTag(i++, step.save());
        tag.put("Steps", tags);
        tag.putInt("Seconds", seconds);
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

    int getFreeSlot() {
        for(int i = 1; i < handler.getSlots(); i++) {
            if(handler.getStackInSlot(i).isEmpty())
                return i;
        }
        return -1;
    }

    public ItemStack insertItem(ItemStack stack) {
        if(seconds >= 3) {
            List<ItemStack> items = new ArrayList<>();
            for(int i = 1; i < handler.getSlots(); i++) {
                items.add(handler.getStackInSlot(i));
                handler.setStackInSlot(i, ItemStack.EMPTY);
            }
            steps.add(new AlchemyRecipe.Step(items, seconds));
        }
        int slot = getFreeSlot();
        if(slot != -1 && isBoiling) {
            isProcessing = true;
            handler.insertItem(slot, stack, false);
            resetSeconds();
            return ItemStack.EMPTY;
        }
        resetSeconds();
        level.setBlockAndUpdate(worldPosition, getBlockState());
        return stack;
    }

    public boolean extractItem(Player player, BlockPos pos) {
        isProcessing = false;
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < handler.getSlots(); i++) {
            if(!handler.getStackInSlot(i).isEmpty())
                items.add(handler.getStackInSlot(i));
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
        if(seconds == 0)
            seconds++;
        steps.add(new AlchemyRecipe.Step(items, seconds));
        resetSeconds();
        AlchemyRecipe recipe = getRecipe();
        ItemStack stack = recipe != null ? recipe.getResultItem(level.registryAccess()) :
                new ItemStack(Items.ALCHEMY_WASTE.get(), level.getRandom().nextInt(1, 3));
        if(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        } else if(player.getInventory().getFreeSlot() != -1) {
            player.addItem(stack);
        } else {
            ItemEntity entity = new ItemEntity(level,
                    pos.getX() + 0.5,
                    pos.getY() + 1,
                    pos.getZ() + 0.5,
                    stack);
            level.addFreshEntity(entity);
        }
        WizzMod.LOGGER.warn(steps);
        steps.clear();
        isBoiling = false;
        level.setBlockAndUpdate(pos, net.supcm.wizz.common.block.Blocks.ALCHEMY_CAULDRON.get().defaultBlockState());
        return false;
    }

    int tick = 0;

    public void tick(BlockState state, BlockPos pos) {
        if (state.getValue(AlchemyCauldronBlock.WATER)) {
            if(level.getBlockState(pos.below()).getBlock() == Blocks.FIRE ||
                    level.getBlockState(pos.below()).getBlock() == Blocks.SOUL_FIRE) {
                isBoiling = true;
            } else
                isBoiling = false;
        }
        if (isProcessing && isBoiling) {
            tick++;
            if (tick % 20 == 0) {
                seconds++;
            }
        }
    }

    public boolean isBoiling() {
        return isBoiling;
    }

    public boolean isProcessing() { return isProcessing; }

    public int getSeconds() {
        return seconds;
    }

    public void resetSeconds() {
        seconds = 0;
        tick = 0;
    }

    public AlchemyRecipe getRecipe() {
        List<AlchemyRecipe> recipes = level.getRecipeManager().getAllRecipesFor(Recipes.ALCHEMY.get());
        for(AlchemyRecipe recipe : recipes) {
            List<AlchemyRecipe.Step> steps = recipe.steps();
            boolean flag = steps.size() == this.steps.size();
            if(flag) {
                for (int i = 0; i < this.steps.size(); i++) {
                    if(!stepMatch(this.steps.get(i), steps.get(i))) {
                        flag = false;
                        break;
                    }
                }
            }
            if(flag) {
                return recipe;
            }
        }
        return null;
    }

    boolean stepMatch(AlchemyRecipe.Step step, AlchemyRecipe.Step recipe) {
        if (Math.abs(step.time() - recipe.time()) <= 1) {
            List<ItemStack> stacks = step.ingredients();
            stacks.removeIf(ItemStack::isEmpty);
            List<ItemStack> tmp = recipe.ingredients();
            boolean flag = true;
            int i = 0;
            for(; i < tmp.size(); i++) {
                ItemStack stack = tmp.get(i);
                ItemStack stack1 = stacks.get(i);
                if (stack.getItem() != stack1.getItem() || stack.getCount() != stack1.getCount()) {
                    flag = false;
                    break;
                }
            }
            if (stacks.size() - 1 > i)
                flag = false;
            return flag;
        }
        return false;
    }
}
