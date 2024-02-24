package net.supcm.wizz.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record ReassessmentRecipe(Ingredient input, List<Integer> concepts, ItemStack output) implements Recipe<SimpleContainer> {
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
    public static class Type implements RecipeType<ReassessmentRecipe> {

    }
    public static class Serializer implements RecipeSerializer<ReassessmentRecipe> {
        @Override
        public Codec<ReassessmentRecipe> codec() {
            return RecordCodecBuilder.create(builder -> builder.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(ReassessmentRecipe::input),
                    Codec.INT.listOf().fieldOf("concepts").forGetter(ReassessmentRecipe::concepts),
                    CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(ReassessmentRecipe::output)
            ).apply(builder, ReassessmentRecipe::new));
        }
        @Override
        public @Nullable ReassessmentRecipe fromNetwork(FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            List<Integer> concepts = new ArrayList<>();
            for(int i = 0; i < 6; i++)
                concepts.add(buffer.readInt());
            ItemStack output = buffer.readItem();
            return new ReassessmentRecipe(input, concepts, output);
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
