package net.supcm.wizz.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record AlchemyRecipe(String daytime, List<Step> steps, ItemStack result, ResourceLocation id)
        implements Recipe<SimpleContainer> {
    @Override public boolean matches(SimpleContainer container, Level level) {
        return true;
    }
    @Override public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return result.copy();
    }
    @Override public boolean canCraftInDimensions(int x, int y) {
        return true;
    }
    @Override public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }
    @Override public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for(Step step : steps)
            ingredients.addAll(step.ingredients.stream().map(Ingredient::of).toList());
        return ingredients;
    }
    @Override public RecipeSerializer<AlchemyRecipe> getSerializer() {
        return Recipes.ALCHEMY_SERIALIZER.get();
    }
    @Override public RecipeType<AlchemyRecipe> getType() {
        return Recipes.ALCHEMY.get();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class Type implements RecipeType<AlchemyRecipe> {

    }

    public static class Serializer implements RecipeSerializer<AlchemyRecipe> {

        @Override
        public AlchemyRecipe fromJson(ResourceLocation id, JsonObject json) {
            String daytime = GsonHelper.getAsString(json, "daytime");
            JsonArray arr = GsonHelper.getAsJsonArray(json, "steps");
            List<Step> steps = new ArrayList<>();
            for(JsonElement element : arr) {
                steps.add(Step.fromJson(element.getAsJsonObject()));
            }
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new AlchemyRecipe(daytime, steps, result, id);
        }

        @Override
        public @Nullable AlchemyRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            String daytime = buffer.readUtf();
            ItemStack stack = buffer.readItem();
            int len = buffer.readIntLE();
            List<Step> steps = new ArrayList<>();
            for(int i = 0; i < len; i++) {
                List<ItemStack> ingredients = new ArrayList<>();
                for(int j = 0; j < buffer.readIntLE(); j++) {
                    ingredients.add(buffer.readItem());
                }
                int time = buffer.readInt();
                steps.add(new Step(ingredients, time));
            }
            return new AlchemyRecipe(daytime, steps, stack, id);
        }
        @Override
        public void toNetwork(FriendlyByteBuf buffer, AlchemyRecipe recipe) {
            buffer.writeUtf(recipe.daytime);
            buffer.writeItemStack(recipe.result, false);
            buffer.writeIntLE(recipe.steps.size());
            for(Step step : recipe.steps) {
                buffer.writeIntLE(step.ingredients.size());
                step.ingredients.forEach(ing -> buffer.writeItemStack(ing, false));
                buffer.writeInt(step.time);
            }
        }
    }
    public record Step(List<ItemStack> ingredients, int time) {

        public static Step fromJson(JsonObject json) {
            int time = GsonHelper.getAsInt(json, "time");
            JsonArray arr = GsonHelper.getAsJsonArray(json, "ingredients");
            List<ItemStack> ingredients = NonNullList.create();
            for(JsonElement element : arr)
                ingredients.add(ShapedRecipe.itemStackFromJson(element.getAsJsonObject()));
            return new Step(ingredients, time);
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Time", time);
            ListTag stacks = new ListTag();
            for (ItemStack ingredient : ingredients) {
                CompoundTag stack = new CompoundTag();
                ingredient.save(stack);
                stacks.add(stack);
            }
            tag.put("Ingredients", stacks);
            return tag;
        }

        public static Step load(CompoundTag tag) {
            int time = tag.getInt("Time");
            ListTag tags = tag.getList("Ingredients", 10);
            List<ItemStack> ingredients = new ArrayList<>();
            for(Tag stack : tags)
                ingredients.add(ItemStack.of((CompoundTag) stack));
            return new Step(ingredients, time);
        }
    }

}
