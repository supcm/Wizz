package net.supcm.wizz.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
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

public record ReassessmentRecipe(Ingredient input, List<Integer> concepts, ItemStack output, ResourceLocation id)
        implements Recipe<SimpleContainer> {
    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Recipes.REASSESSMENT_SERIALIZER.get();
    }

    @Override
    public RecipeType<ReassessmentRecipe> getType() {
        return Recipes.REASSESSMENT.get();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class Type implements RecipeType<ReassessmentRecipe> {

    }
    public static class Serializer implements RecipeSerializer<ReassessmentRecipe> {
        @Override
        public ReassessmentRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray arr = GsonHelper.getAsJsonArray(json, "concepts");
            List<Integer> concepts = new ArrayList<>();
            for(JsonElement element : arr)
                concepts.add(element.getAsInt());
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new ReassessmentRecipe(ingredient, concepts, result, id);
        }
        @Override
        public @Nullable ReassessmentRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            List<Integer> concepts = new ArrayList<>();
            for(int i = 0; i < 6; i++)
                concepts.add(buffer.readInt());
            ItemStack output = buffer.readItem();
            return new ReassessmentRecipe(input, concepts, output, id);
        }
        @Override
        public void toNetwork(FriendlyByteBuf buffer, ReassessmentRecipe recipe) {
            recipe.input.toNetwork(buffer);
            for(Integer c : recipe.concepts())
                buffer.writeInt(c);
            buffer.writeItemStack(recipe.output(), false);
        }
    }
}
