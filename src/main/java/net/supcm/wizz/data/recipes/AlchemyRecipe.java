package net.supcm.wizz.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record AlchemyRecipe(String daytime, List<Step> steps, ItemStack result) implements Recipe<SimpleContainer> {
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
    public static class Type implements RecipeType<AlchemyRecipe> {

    }

    public static class Serializer implements RecipeSerializer<AlchemyRecipe> {
        @Override
        public Codec<AlchemyRecipe> codec() {
            return RecordCodecBuilder.create(builder -> builder.group(
                Codec.STRING.optionalFieldOf("daytime", "day").forGetter(AlchemyRecipe::daytime),
                Step.CODEC.listOf().fieldOf("steps").forGetter(AlchemyRecipe::steps),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(AlchemyRecipe::result)
            ).apply(builder, AlchemyRecipe::new));
        }
        @Override
        public @Nullable AlchemyRecipe fromNetwork(FriendlyByteBuf buffer) {
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
            return new AlchemyRecipe(daytime, steps, stack);
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
        public static final Codec<Step> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.listOf().fieldOf("ingredients")
                        .forGetter(Step::ingredients),
                Codec.INT.fieldOf("time").forGetter(Step::time)
        ).apply(builder, Step::new));

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
