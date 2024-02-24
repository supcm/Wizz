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
import net.minecraft.world.item.crafting.Ingredient;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.block.Blocks;
import net.supcm.wizz.common.item.Items;
import net.supcm.wizz.data.recipes.AlchemyRecipe;
import net.supcm.wizz.data.recipes.GrindingRecipe;

public class AlchemyCategory implements IRecipeCategory<AlchemyRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(WizzMod.MODID, "alchemy");
    public static final ResourceLocation TEXTURE = new ResourceLocation(WizzMod.MODID,
            "textures/gui/alchemy_jei.png");
    public static final RecipeType<AlchemyRecipe> TYPE = new RecipeType<>(UID, AlchemyRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;
    public AlchemyCategory(IGuiHelper gui) {
        this.background = gui.createDrawable(TEXTURE, 0, 0, 128, 164);
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.ALCHEMY_CAULDRON.get()));
    }
    @Override public RecipeType<AlchemyRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("block.wizz.alchemy_cauldron"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, AlchemyRecipe recipe, IFocusGroup focuses) {
        for(int y = 0; y < recipe.steps().size(); y++) {
            AlchemyRecipe.Step step = recipe.steps().get(y);
            for(int x = 0; x < step.ingredients().size(); x++) {
                builder.addSlot(RecipeIngredientRole.INPUT, 6 + 20 * x, 9 + 30 * y)
                        .addIngredients(Ingredient.of(step.ingredients().get(x)));
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 144).addItemStack(recipe.result());
    }
    @Override public void draw(AlchemyRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gui,
                     double mouseX, double mouseY) {
        for(int y = 0; y < recipe.steps().size(); y++) {
            AlchemyRecipe.Step step = recipe.steps().get(y);
            gui.blit(TEXTURE, 4, 6 + 30 * y, 128, 0, 120, 28);
            gui.pose().pushPose();
            gui.pose().scale(0.75f, 0.75f, 0.75f);
            gui.drawCenteredString(Minecraft.getInstance().font,
                    step.time() + "s", 86, 36 + 40 * y + (y > 0 ? 1 : 0), 0xFFFFFF);
            gui.pose().popPose();
        }
        IRecipeCategory.super.draw(recipe, recipeSlotsView, gui, mouseX, mouseY);
    }
}
