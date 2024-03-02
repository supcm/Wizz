package net.supcm.wizz.client.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.block.Blocks;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.data.recipes.ReassessmentRecipe;

import java.util.ArrayList;
import java.util.List;

public class ReassessmentCategory implements IRecipeCategory<ReassessmentRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(WizzMod.MODID, "reassessment");
    public static final ResourceLocation TEXTURE = new ResourceLocation(WizzMod.MODID,
            "textures/gui/reassessment_jei.png");
    public static final RecipeType<ReassessmentRecipe> TYPE = new RecipeType<>(UID, ReassessmentRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;
    public ReassessmentCategory(IGuiHelper gui) {
        this.background = gui.createDrawable(TEXTURE, 0, 0, 139, 78);
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.REASSESSMENT_TABLE.get()));
    }
    @Override public RecipeType<ReassessmentRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei.reassessment"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, ReassessmentRecipe recipe, IFocusGroup focuses) {
        int ind = 0;
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 31).addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.INPUT, 79, 31).addItemStack(new ItemStack(Items.CRYSTAL.get()));
        if(recipe.concepts().get(ind) > 0)
            builder.addSlot(RecipeIngredientRole.INPUT, 30, 3).addItemStack(getConcept(recipe, ind));
        ind++;
        if(recipe.concepts().get(ind) > 0)
            builder.addSlot(RecipeIngredientRole.INPUT, 3, 19).addItemStack(getConcept(recipe, ind));
        ind++;
        if(recipe.concepts().get(ind) > 0)
            builder.addSlot(RecipeIngredientRole.INPUT, 3, 43).addItemStack(getConcept(recipe, ind));
        ind++;
        if(recipe.concepts().get(ind) > 0)
            builder.addSlot(RecipeIngredientRole.INPUT, 57, 19).addItemStack(getConcept(recipe, ind));
        ind++;
        if(recipe.concepts().get(ind) > 0)
            builder.addSlot(RecipeIngredientRole.INPUT, 57, 43).addItemStack(getConcept(recipe, ind));
        ind++;
        if(recipe.concepts().get(ind) > 0)
            builder.addSlot(RecipeIngredientRole.INPUT, 30, 59).addItemStack(getConcept(recipe, ind));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 31).addItemStack(recipe.output());
    }

    private final List<Item> CONCEPTS = new ArrayList<>();
    {
        CONCEPTS.add(Items.CONCEPT_BEAUTY.get());
        CONCEPTS.add(Items.CONCEPT_CREATION.get());
        CONCEPTS.add(Items.CONCEPT_ART.get());
        CONCEPTS.add(Items.CONCEPT_TRUTH.get());
        CONCEPTS.add(Items.CONCEPT_SOUL.get());
        CONCEPTS.add(Items.CONCEPT_LIES.get());
    }
    private ItemStack getConcept(ReassessmentRecipe recipe, int ind) {

        return new ItemStack(CONCEPTS.get(ind), recipe.concepts().get(ind));
    }

    @Override public void draw(ReassessmentRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }
}
