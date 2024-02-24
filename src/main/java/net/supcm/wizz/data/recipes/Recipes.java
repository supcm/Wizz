package net.supcm.wizz.data.recipes;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.supcm.wizz.WizzMod;

public class Recipes {
    public static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES,
            WizzMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,
            WizzMod.MODID);

    public static final RegistryObject<EnchantingRecipe.Type> ENCHANTING
            = RECIPES.register("enchanting", EnchantingRecipe.Type::new);
    public static final RegistryObject<EnchantingRecipe.Serializer> ENCHANTING_SERIALIZER
            = SERIALIZERS.register("enchanting", EnchantingRecipe.Serializer::new);
    public static final RegistryObject<ConceptRecipe.Type> CONCEPT
            = RECIPES.register("concept", ConceptRecipe.Type::new);
    public static final RegistryObject<ConceptRecipe.Serializer> CONCEPT_SERIALIZER
            = SERIALIZERS.register("concept", ConceptRecipe.Serializer::new);
    public static final RegistryObject<ReassessmentRecipe.Type> REASSESSMENT
            = RECIPES.register("reassessment", ReassessmentRecipe.Type::new);
    public static final RegistryObject<ReassessmentRecipe.Serializer> REASSESSMENT_SERIALIZER
            = SERIALIZERS.register("reassessment", ReassessmentRecipe.Serializer::new);
    public static final RegistryObject<AlchemyRecipe.Type> ALCHEMY
            = RECIPES.register("alchemy", AlchemyRecipe.Type::new);
    public static final RegistryObject<AlchemyRecipe.Serializer> ALCHEMY_SERIALIZER
            = SERIALIZERS.register("alchemy", AlchemyRecipe.Serializer::new);
    public static final RegistryObject<GrindingRecipe.Type> GRINDING
            = RECIPES.register("grinding", GrindingRecipe.Type::new);
    public static final RegistryObject<GrindingRecipe.Serializer> GRINDING_SERIALIZER
            = SERIALIZERS.register("grinding", GrindingRecipe.Serializer::new);
}
