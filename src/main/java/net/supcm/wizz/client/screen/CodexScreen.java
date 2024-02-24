package net.supcm.wizz.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.WizzModConfig;

import java.awt.geom.Rectangle2D;
import java.util.*;


public class CodexScreen extends Screen {
    private final ResourceLocation TEXTURE = new ResourceLocation(WizzMod.MODID,
            "textures/gui/codex_gui.png");
    int currentPage = 0;
    private PageButton forwardButton;
    private PageButton backButton;
    private final Map<String, Enchantment> toDisplay = new HashMap<>();
    List<Page> pages = new ArrayList<>();
    public CodexScreen(List<Tag> enchantments) {
        super(Component.translatable("item.wizz.codex"));
        for(Tag tag : enchantments) {
            String glyphs = tag.getAsString().split("'")[0];
            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(
                    tag.getAsString().split("'")[1]));
            toDisplay.put(glyphs, ench);
        }
        List<Map.Entry<String, Enchantment>> list = new ArrayList<>(toDisplay.entrySet());
        int i = 0;
        for(int l = 0; l <= getPages(); l++) {
            Page page = new Page();
            for(int j = 0; j < 4; j++) {
                if(i < list.size()) {
                    page.add(list.get(i));
                    i++;
                }
            }
            pages.add(page);
        }
    }
    int getPages() { return (int)Math.ceil(toDisplay.size()/4.0); }
    @Override protected void init() {
        createButtons();
    }
    void createButtons() {
        clearWidgets();
        forwardButton = addRenderableWidget(new PageButton((width - 192) / 2 + 174, 159, true,
                (action) -> pageForward(), true));
        backButton = addRenderableWidget(new PageButton((width - 192) / 2 - 14, 159, false,
                (action) -> pageBack(), true));
        addRenderableWidget(Button.builder(Component.translatable("gui.done"),
                        (action) -> onClose())
                .bounds((width - 192) / 2 + 2, 196, 200, 20).build());
        updateButtonVisibility();
    }
    private void updateButtonVisibility() {
        forwardButton.visible = currentPage < getPages() - 1;
        backButton.visible = currentPage > 0;
    }
    @Override public boolean keyPressed(int key, int x, int y) {
        if (super.keyPressed(key, x, y))
            return true;
        else
            switch (key) {
                case 266 -> {
                    this.backButton.onPress();
                    return true;
                }
                case 267 -> {
                    this.forwardButton.onPress();
                    return true;
                }
                default -> {
                    return false;
                }
            }
    }
    protected void pageBack() {
        if (currentPage > 0)
            --currentPage;
        updateButtonVisibility();
    }
    protected void pageForward() {
        if (currentPage < getPages() - 1)
            ++currentPage;
        updateButtonVisibility();
    }

    @Override
    public void renderBackground(GuiGraphics gui, int p_299421_, int p_298679_, float p_297268_) {
        super.renderBackground(gui, p_299421_, p_298679_, p_297268_);
        gui.blit(TEXTURE, (width - 192) / 2 - 36, 2, 0, 0, 256, 192);
    }

    @Override public void render(GuiGraphics gui, int x, int y, float tick) {
        renderBackground(gui, x, y, tick);
        //RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int i = 0;
        for(Map.Entry<String, Enchantment> entry : pages.get(currentPage).getEntries()){
            if(WizzModConfig.ENABLE_DESCRIPTIONS_IN_CODEX.get() ||
                    ModList.get().isLoaded("enchdesc")) {
                List<Component> list = new ArrayList<>();
                Component desc = Component.translatable(entry.getValue().getDescriptionId() + ".desc");
                Component name = Component.translatable(entry.getValue().getDescriptionId())
                        .withStyle(Style.EMPTY.withColor(entry.getValue().isCurse() ? ChatFormatting.RED.getColor() : 0x561185));
                list.add(name);
                list.add(desc);
                if (isMouseAt(new Rectangle2D.Double((width - 192) / 2 - 20, 30 + i * 28,
                        font.width(name), font.lineHeight + 3), x, y))
                    gui.renderTooltip(minecraft.font, list, Optional.empty(), x, y);
            }
            if (!entry.getKey().contains("_")) {
                ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(WizzMod.MODID, entry.getKey())));
                gui.renderFakeItem(stack,
                        (width - 192) / 2 + 110, 28 + i*28);
            } else {
                String[] glyphs = entry.getKey().split("_");
                ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(WizzMod.MODID, glyphs[0])));
                ItemStack stack1 = new ItemStack(ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(WizzMod.MODID, glyphs[1])));
                gui.renderFakeItem(stack,
                        (width - 192) / 2 + 110, 28 + i*28);
                gui.renderFakeItem(stack1,
                        (width - 192) / 2 + 134, 28 + i*28);
                if (glyphs.length == 3) {
                    ItemStack stack2 = new ItemStack(ForgeRegistries.ITEMS.getValue(
                            new ResourceLocation(WizzMod.MODID, glyphs[2])));
                    gui.renderFakeItem(stack2,
                            (width - 192) / 2 + 158, 28 + i*28);
                }
            }
            gui.drawString(minecraft.font, Component.translatable(entry.getValue().getDescriptionId()),
                    (width - 192) / 2 - 20, 32 + i*28, entry.getValue().isCurse() ? ChatFormatting.RED.getColor() : 0x561185);
            i++;
            if(i == 4) i = 0;
        }
        gui.drawString(minecraft.font, Component.literal((currentPage + 1) + "/" + getPages()),
                (width - 192) / 2 + 62, 159, 0xffffff);
        super.render(gui, x, y, tick);
    }
    public boolean isMouseAt(Rectangle2D rect, double mouseX, double mouseY) {
        if (minecraft != null && minecraft.screen == this)
            return rect.contains((int) mouseX, (int) mouseY);
        return false;
    }
    private static class Page {
        public List<Map.Entry<String, Enchantment>> entries = new ArrayList<>();
        public List<Map.Entry<String, Enchantment>> getEntries() { return entries; }
        Page add(Map.Entry<String, Enchantment> entry) {
            entries.add(entry);
            return this;
        }
    }
}
