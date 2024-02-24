package net.supcm.wizz.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record ConceptRecipe(List<Ingredient> ingredients, int level, ItemStack output) implements Recipe<SimpleContainer> {
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
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.addAll(this.ingredients);
        return ingredients;
    }

    @Override
    public RecipeSerializer<ConceptRecipe> getSerializer() {
        return Recipes.CONCEPT_SERIALIZER.get();
    }

    @Override
    public RecipeType<ConceptRecipe> getType() {
        return Recipes.CONCEPT.get();
    }
    public static class Type implements RecipeType<ConceptRecipe> {

    }

    public static class Serializer implements RecipeSerializer<ConceptRecipe> {
        @Override
        public Codec<ConceptRecipe> codec() {
            return RecordCodecBuilder.create(builder -> builder.group(
                    Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(ConceptRecipe::ingredients),
                    Codec.INT.fieldOf("level").forGetter(ConceptRecipe::level),
                    CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(ConceptRecipe::output)
            ).apply(builder, ConceptRecipe::new));
        }
        @Override
        public @Nullable ConceptRecipe fromNetwork(FriendlyByteBuf buffer) {
            int len = buffer.readIntLE();
            List<Ingredient> input = new ArrayList<>();
            for(int i = 0; i < len; i++)
                input.add(Ingredient.fromNetwork(buffer));
            ItemStack output = buffer.readItem();
            int level = buffer.readInt();
            return new ConceptRecipe(input, level, output);
        }
        @Override
        public void toNetwork(FriendlyByteBuf buffer, ConceptRecipe recipe) {
            int len = recipe.ingredients.size();
            buffer.writeIntLE(len);
            for(Ingredient ing : recipe.ingredients())
                ing.toNetwork(buffer);
            buffer.writeItemStack(recipe.output(), false);
            buffer.writeInt(recipe.level());
        }
    }
}
