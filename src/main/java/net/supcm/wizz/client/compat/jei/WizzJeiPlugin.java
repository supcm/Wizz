package net.supcm.wizz.client.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.block.Blocks;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.data.recipes.Recipes;

@JeiPlugin
public class WizzJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() { return new ResourceLocation(WizzMod.MODID, "jei_plugin"); }
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new EnchantingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ConceptCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ReassessmentCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new GrindingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AlchemyCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(EnchantingCategory.TYPE, manager.getAllRecipesFor(Recipes.ENCHANTING.get())
                .stream().toList());
        registration.addRecipes(ConceptCategory.TYPE, manager.getAllRecipesFor(Recipes.CONCEPT.get())
                .stream().toList());
        registration.addRecipes(ReassessmentCategory.TYPE, manager.getAllRecipesFor(Recipes.REASSESSMENT.get())
                .stream().toList());
        registration.addRecipes(GrindingCategory.TYPE, manager.getAllRecipesFor(Recipes.GRINDING.get())
                .stream().toList());
        registration.addRecipes(AlchemyCategory.TYPE, manager.getAllRecipesFor(Recipes.ALCHEMY.get())
                .stream().toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.ENCHANTED_TABLE.get()), EnchantingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.WORD_MACHINE.get()), EnchantingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.WORD_FORGE.get()), EnchantingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.THOUGHT_LOOM.get()), ConceptCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.REASSESSMENT_TABLE.get()), ReassessmentCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.REASSESSMENT_PILLAR.get()), ReassessmentCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.MORTAR.get()), GrindingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.PESTLE.get()), GrindingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.ALCHEMY_CAULDRON.get()), AlchemyCategory.TYPE);
    }
}
