package net.supcm.wizz.common.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.wizz.common.block.Blocks;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.data.recipes.ReassessmentRecipe;
import net.supcm.wizz.data.recipes.Recipes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReassessmentTableBlockEntity extends BlockEntity {
    private ReassessmentRecipe recipe = null;
    public boolean isValid;
    public List<Integer> concepts = createEmptyConcepts();

    public Map<BlockPos, ItemStack> pillars = Maps.newHashMap();
    public final ItemStackHandler handler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) {
            updateRecipe();
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
        }
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return true; }
        @Override protected int getStackLimit(int slot, @Nonnull ItemStack stack) { return 1; }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    @Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? inventory.cast() : super.getCapability(cap);
    }
    public ReassessmentTableBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntities.REASSESSMENT_TABLE.get(), p_155229_, p_155230_);
    }
    @Override public void invalidateCaps() {
        super.invalidateCaps();
        inventory.invalidate();
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag);
        handler.deserializeNBT(tag.getCompound("Inventory"));
        isValid = tag.getBoolean("IsValid");
        List<Integer> list = new ArrayList<>();
        for(int i : tag.getIntArray("Concepts"))
            list.add(i);
        concepts = list;
    }
    @Override public void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", handler.serializeNBT());
        tag.putBoolean("IsValid", isValid);
        tag.putIntArray("Concepts", concepts);
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
    public ReassessmentRecipe getRecipe() { return recipe; }
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
    int i = 0;
    public void tick() {
        if(level != null){
            validate(worldPosition, i == 0);
            i++;
            if (!level.isClientSide && isValid)
                if (recipe == null && !handler.getStackInSlot(0).isEmpty())
                    updateRecipe();
        }
    }
    public void createResult() {
        if(recipe != null) {
            for(BlockPos pos : pillars.keySet())
                if(level.getBlockEntity(pos) instanceof ReassessmentPillarBlockEntity tile)
                    tile.handler.setStackInSlot(0, tile.handler.getStackInSlot(0));
            List<Integer> concepts = recipe.concepts();
            handler.setStackInSlot(0, recipe.getResultItem(level.registryAccess()));
            for(ItemStack stack : pillars.values()) {
                if(stack.getItem() == Items.CONCEPT_BEAUTY.get())
                    stack.shrink(concepts.get(0));
                else if(stack.getItem() == Items.CONCEPT_CREATION.get())
                    stack.shrink(concepts.get(1));
                else if(stack.getItem() == Items.CONCEPT_ART.get())
                    stack.shrink(concepts.get(2));
                else if(stack.getItem() == Items.CONCEPT_TRUTH.get())
                    stack.shrink(concepts.get(3));
                else if(stack.getItem() == Items.CONCEPT_SOUL.get())
                    stack.shrink(concepts.get(4));
                else
                    stack.shrink(concepts.get(5));
            }
        }
        updateRecipe();
    }
    private List<Integer> createConcepts() {
        List<Integer> concepts = createEmptyConcepts();
        for(ItemStack stack : pillars.values()) {
            if(stack.getItem() == Items.CONCEPT_BEAUTY.get()){
                concepts.set(0, stack.getCount());
            } else if(stack.getItem() == Items.CONCEPT_CREATION.get()){
                concepts.set(1, stack.getCount());
            } else if(stack.getItem() == Items.CONCEPT_ART.get()){
                concepts.set(2, stack.getCount());
            } else if(stack.getItem() == Items.CONCEPT_TRUTH.get()){
                concepts.set(3, stack.getCount());
            } else if(stack.getItem() == Items.CONCEPT_SOUL.get()){
                concepts.set(4, stack.getCount());
            } else if(stack.getItem() == Items.CONCEPT_LIES.get())
                concepts.set(5, stack.getCount());
        }
        return concepts;
    }
    public void updateRecipe() {
        if(isValid){
            boolean setLeastOne = false;
            List<Integer> concepts = createConcepts();
            List<RecipeHolder<ReassessmentRecipe>> recipes = level.getRecipeManager()
                    .getAllRecipesFor(Recipes.REASSESSMENT.get());
            for (RecipeHolder<ReassessmentRecipe> holder : recipes) {
                ReassessmentRecipe recipe = holder.value();
                if (recipe.input().test(handler.getStackInSlot(0))) {
                    boolean isVal = true;
                    for (int i = 0; i < concepts.size(); i++) {
                        if (recipe.concepts().get(i) != 0 &&
                                concepts.get(i) < recipe.concepts().get(i)) {
                            isVal = false;
                            break;
                        }
                    }
                    if (isVal) {
                        this.recipe = recipe;
                        this.concepts = recipe.concepts();
                        setLeastOne = true;
                    }
                }
            }
            if(!setLeastOne) {
                recipe = null;
                this.concepts = createEmptyConcepts();
            }
            for(BlockPos pos : pillars.keySet())
                if(level.getBlockEntity(pos) instanceof ReassessmentPillarBlockEntity tile) {
                    tile.setChanged();
                }
        }
    }
    public void invalidatePillars() {
        for(BlockPos pos : pillars.keySet()) {
            if(level.getBlockEntity(pos) instanceof ReassessmentPillarBlockEntity tile){
                tile.setLinkedTable(null);
            }
        }
        pillars.clear();
        updateRecipe();
    }
    private void changeValid(boolean valid, BlockPos[] pillarsPoses) {
        isValid = valid;
        if(valid){
            for (BlockPos pos : pillarsPoses) {
                ReassessmentPillarBlockEntity tile = (ReassessmentPillarBlockEntity)level.getBlockEntity(pos);
                tile.setLinkedTable(this);
                pillars.put(pos, tile.handler.getStackInSlot(0));
                updateRecipe();
            }
        } else {
            invalidatePillars();
        }
    }
    public void validate(BlockPos pos, boolean isFirstStart) {
        boolean valid = true;
        BlockPos[] pillarsPoses = new BlockPos[6];
        int k = 0;
        for(int i = -2; i < 3; i++)
            for(int j = -2; j < 3; j++) {
                if((i == -2 || i == 2) && j == 0) {
                    if(level.getBlockState(pos.north(i)).getBlock() != Blocks.REASSESSMENT_PILLAR.get())
                        valid = false;
                    else {
                        pillarsPoses[k] = new BlockPos(pos.north(i));
                        k++;
                    }
                } else if((i == -1 || i == 1) && (j == -2 || j == 2)) {
                    if(level.getBlockState(pos.north(i).west(j)).getBlock() != Blocks.REASSESSMENT_PILLAR.get())
                        valid = false;
                    else {
                        pillarsPoses[k] = new BlockPos(pos.north(i).west(j));
                        k++;
                    }
                } else {
                    if (!level.getBlockState(pos.north(i).west(j)).isAir() &&
                            !(i == 0 && j == 0))
                        valid = false;
                }
            }
        if(isFirstStart) {
            changeValid(isValid, pillarsPoses);
            updateRecipe();
        }
        if(!valid && isValid) changeValid(false, pillarsPoses);
        else if(valid && !isValid) changeValid(true, pillarsPoses);
    }

    List<Integer> createEmptyConcepts() {
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < 6; i++)
            list.add(0);
        return list;
    }
}
