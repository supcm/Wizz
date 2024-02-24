package net.supcm.wizz.common.network.packets;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.supcm.wizz.common.block.Blocks;
import net.supcm.wizz.common.block.entity.MortarBlockEntity;
import net.supcm.wizz.data.recipes.GrindingRecipe;
import net.supcm.wizz.data.recipes.Recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MortarRecipePacket {
    public final BlockPos pos;
    public final String mode;
    public MortarRecipePacket(BlockPos pos, String mode) {
        this.pos = pos;
        this.mode = mode;
    }
    public static MortarRecipePacket load(FriendlyByteBuf buffer){
        return new MortarRecipePacket(buffer.readBlockPos(), buffer.readUtf());
    }
    public void save(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeUtf(mode);
    }
    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerLevel level = ctx.getSender().serverLevel();
            MortarBlockEntity tile = (MortarBlockEntity) level.getBlockEntity(pos);
            for(RecipeHolder<GrindingRecipe> holder : level.getRecipeManager().getAllRecipesFor(Recipes.GRINDING.get())) {
                GrindingRecipe recipe = holder.value();
                if(recipe.mode().equals(mode)) {
                    //boolean flag = true;
                    if(recipe.ingredients().size() != getNonEmptySlots(tile))
                        continue;
                    /*int i = 0;
                    for(Ingredient ing : recipe.ingredients()) {
                        if(!ing.test(tile.handler.getStackInSlot(i))) {
                            flag = false;
                            break;
                        }
                        i++;
                    }*/
                    List<ItemStack> stacks = new ArrayList<>();
                    for(int slot = 0; slot < tile.handler.getSlots(); slot++) {
                        if(!tile.handler.getStackInSlot(slot).isEmpty())
                            stacks.add(tile.handler.getStackInSlot(slot));
                    }
                    if(matchesIngredients(stacks, recipe.ingredients())) {
                        ItemStack stack = recipe.result();
                        level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5,
                                pos.getY() + 0.35, pos.getZ() + 0.5, stack,
                                0, 0.5, 0));
                    }
                }
            }
            for(int slot = 0; slot < tile.handler.getSlots(); slot++) {
                ItemStack stack = tile.handler.getStackInSlot(slot);
                stack.shrink(1);
                tile.handler.setStackInSlot(slot, stack);
            }
        });

        ctx.setPacketHandled(true);
    }
    int getNonEmptySlots(MortarBlockEntity tile) {
        int count = 0;
        for(int i = 0; i < tile.handler.getSlots(); i++) {
            if(!tile.handler.getStackInSlot(i).isEmpty())
                count++;
        }
        return count;
    }
    boolean matchesIngredients(List<ItemStack> inventory, List<Ingredient> ingredients) {
        for(ItemStack stack : inventory) {
            boolean flag = false;
            for(Ingredient ing : ingredients) {
                ItemStack[] stacks = ing.getItems();
                if(Arrays.stream(stacks).anyMatch(item -> item.getItem() == stack.getItem())) {
                    flag = true;
                    break;
                }
            }
            if(!flag)
                return false;
        }
        return true;
    }
}
