package net.supcm.wizz.client.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
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
import net.supcm.wizz.data.recipes.EnchantingRecipe;

public class EnchantingCategory implements IRecipeCategory<EnchantingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(WizzMod.MODID, "enchanting");
    public static final ResourceLocation TEXTURE = new ResourceLocation(WizzMod.MODID,
            "textures/gui/enchanting_jei.png");
    public static final RecipeType<EnchantingRecipe> TYPE = new RecipeType<>(UID, EnchantingRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;
    public EnchantingCategory(IGuiHelper gui) {
        this.background = gui.createDrawable(TEXTURE, 0, 0, 100, 40);
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.ENCHANTED_TABLE.get()));
    }
    @Override public RecipeType<EnchantingRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei.enchanting"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, EnchantingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 12).addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 12).addItemStack(recipe.output());
    }
    @Override public void draw(EnchantingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        if(recipe.level() != -1) {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, Integer.toString(recipe.level()),
                    50, 6, 0x14d924);
        }
        if(recipe.tier() == 0) {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font,
                    Component.translatable("block.wizz.enchanted_table"),
                    50, 31, 0xFF6347);
        } else if(recipe.tier() == 1) {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font,
                    Component.translatable("block.wizz.word_machine"),
                    50, 31, 0xFF6347);
        } else {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font,
                    Component.translatable("block.wizz.word_forge"),
                    50, 31, 0xFF6347);
        }
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }
}
