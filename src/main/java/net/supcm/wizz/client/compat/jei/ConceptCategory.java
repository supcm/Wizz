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

public class ConceptCategory implements IRecipeCategory<ConceptRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(WizzMod.MODID, "concept");
    public static final ResourceLocation TEXTURE = new ResourceLocation(WizzMod.MODID,
            "textures/gui/concept_jei.png");
    public static final RecipeType<ConceptRecipe> TYPE = new RecipeType<>(UID, ConceptRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;
    public ConceptCategory(IGuiHelper gui) {
        this.background = gui.createDrawable(TEXTURE, 0, 0, 128, 70);
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.THOUGHT_LOOM.get()));
    }
    @Override public RecipeType<ConceptRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei.concept"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, ConceptRecipe recipe, IFocusGroup focuses) {
        int ind = 0;
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 27).addIngredients(recipe.getIngredients().get(ind++));
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 5).addIngredients(recipe.getIngredients().get(ind++));
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 27).addIngredients(recipe.getIngredients().get(ind++));
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 49).addIngredients(recipe.getIngredients().get(ind));
        builder.addSlot(RecipeIngredientRole.INPUT, 83, 35).addItemStack(new ItemStack(Items.CONCEPT_BASE.get()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 106, 27).addItemStack(recipe.output());
    }
    @Override public void draw(ConceptRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        if(recipe.level() != -1)
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, Integer.toString(recipe.level()),
                    89, 20, 0x14d924);
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }
}
