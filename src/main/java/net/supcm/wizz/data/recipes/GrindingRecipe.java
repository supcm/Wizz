package net.supcm.wizz.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record GrindingRecipe(String mode, List<Ingredient> ingredients, ItemStack result) implements Recipe<SimpleContainer> {
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
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(ingredients);
        return list;
    }
    @Override public RecipeSerializer<GrindingRecipe> getSerializer() {
        return Recipes.GRINDING_SERIALIZER.get();
    }
    @Override public RecipeType<GrindingRecipe> getType() {
        return Recipes.GRINDING.get();
    }
    public static class Type implements RecipeType<GrindingRecipe> {

    }
    public static class Serializer implements RecipeSerializer<GrindingRecipe> {
        @Override
        public Codec<GrindingRecipe> codec() {
            return RecordCodecBuilder.create(builder -> builder.group(
                    Codec.STRING.fieldOf("mode").forGetter(GrindingRecipe::mode),
                    Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(GrindingRecipe::ingredients),
                    CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(GrindingRecipe::result)
            ).apply(builder, GrindingRecipe::new));
        }
        @Override
        public @Nullable GrindingRecipe fromNetwork(FriendlyByteBuf buffer) {
            String mode = buffer.readUtf();
            ItemStack result = buffer.readItem();
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for(int i = 0; i < buffer.readIntLE(); i++)
                ingredients.add(Ingredient.fromNetwork(buffer));
            return new GrindingRecipe(mode, ingredients, result);
        }
        @Override
        public void toNetwork(FriendlyByteBuf buffer, GrindingRecipe recipe) {
            buffer.writeUtf(recipe.mode);
            buffer.writeItemStack(recipe.result, false);
            buffer.writeIntLE(recipe.ingredients.size());
            for(Ingredient ing : recipe.ingredients)
                ing.toNetwork(buffer);
        }
    }
}
