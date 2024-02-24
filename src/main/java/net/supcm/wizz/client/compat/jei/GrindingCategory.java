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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.block.Blocks;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.data.recipes.ConceptRecipe;
import net.supcm.wizz.data.recipes.GrindingRecipe;

public class GrindingCategory implements IRecipeCategory<GrindingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(WizzMod.MODID, "grinding");
    public static final ResourceLocation TEXTURE = new ResourceLocation(WizzMod.MODID,
            "textures/gui/grinding_jei.png");
    public static final RecipeType<GrindingRecipe> TYPE = new RecipeType<>(UID, GrindingRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;
    public GrindingCategory(IGuiHelper gui) {
        this.background = gui.createDrawable(TEXTURE, 0, 0, 157, 21);
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.PESTLE.get()));
    }
    @Override public RecipeType<GrindingRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("block.wizz.mortar"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, GrindingRecipe recipe, IFocusGroup focuses) {
        for(int i = 0; i < recipe.ingredients().size(); i++)
            builder.addSlot(RecipeIngredientRole.INPUT, 3 + i * 22, 3).addIngredients(recipe.ingredients().get(i));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 138, 3).addItemStack(recipe.result());
    }
    @Override public void draw(GrindingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gui,
                     double mouseX, double mouseY) {
        if(recipe.mode().equals("grind"))
            gui.blit(TEXTURE, 112, 6, 0, 22, 20, 10);
        else
            gui.blit(TEXTURE, 116, 1, 20, 22, 10, 20);

        IRecipeCategory.super.draw(recipe, recipeSlotsView, gui, mouseX, mouseY);
    }
}
