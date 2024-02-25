package net.supcm.wizz.data.recipes;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record EnchantingRecipe(Ingredient input, int level, short tier, ItemStack output, ResourceLocation id)
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
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Recipes.ENCHANTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<EnchantingRecipe> getType() {
        return Recipes.ENCHANTING.get();
    }

    public static class Type implements RecipeType<EnchantingRecipe> {

    }

    public static class Serializer implements RecipeSerializer<EnchantingRecipe> {
        @Override
        public EnchantingRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            int level = GsonHelper.getAsInt(json, "level");
            short tier = GsonHelper.getAsShort(json, "level");
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            return new EnchantingRecipe(ingredient, level, tier, result, id);
        }

        @Override
        public @Nullable EnchantingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            int level = buffer.readInt();
            short tier = buffer.readShort();
            return new EnchantingRecipe(input, level, tier, output, id);
        }
        @Override
        public void toNetwork(FriendlyByteBuf buffer, EnchantingRecipe recipe) {
            recipe.input().toNetwork(buffer);
            buffer.writeItemStack(recipe.output(), false);
            buffer.writeInt(recipe.level());
            buffer.writeShort(recipe.tier());
        }
    }
}
