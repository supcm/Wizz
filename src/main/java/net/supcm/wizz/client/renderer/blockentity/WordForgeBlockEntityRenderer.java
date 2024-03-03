package net.supcm.wizz.client.renderer.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.client.RenderingHelper;
import net.supcm.wizz.common.block.entity.WordForgeBlockEntity;
import net.supcm.wizz.common.handler.EnchantmentsHandler;
import net.supcm.wizz.common.item.Items;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class WordForgeBlockEntityRenderer implements BlockEntityRenderer<WordForgeBlockEntity> {
    private final ResourceLocation EFFECT_TEXTURE = new ResourceLocation(WizzMod.MODID,
            "textures/block/reassessment_circle.png");
    float s = 0.55F;
    private static List<String> T2_LIST = new ArrayList<>();
    public static List<String> T3_LIST = new ArrayList<>();
    public static void setListT2(List<String> list) { T2_LIST = list; }
    public static void setListT3(List<String> list) { T3_LIST = list; }
    public WordForgeBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
    @Override public void render(WordForgeBlockEntity te, float partialTicks, PoseStack ms,
                                 MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = te.handler.getStackInSlot(0);
        ItemStack stack1 = te.handler.getStackInSlot(1);
        ItemStack stack2 = te.handler.getStackInSlot(2);
        float time = te.getLevel().getGameTime() + partialTicks;
        /*TODO 1.1: fix no warranty on client
        if(te.warranty) {
            renderEffect(ms, buffer, te, 1, 0.23f, 0.23f);
        }*/
        {
            renderGlyphs(te, ms, buffer, combinedLight, combinedOverlay);
            if (te.enchLevel != -1 && (!stack.isEmpty() || !stack1.isEmpty() || !stack2.isEmpty())) {
                ms.pushPose();
                ms.translate(0.5, 1.85, 0.5);
                ms.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
                ms.scale(-0.025f, -0.025f, 0.025f);
                Font fontrenderer = Minecraft.getInstance().font;
                String text = String.valueOf(te.enchLevel);
                float width = (float) (-fontrenderer.width(text) / 2);
                Matrix4f text_matrix = ms.last().pose();
                fontrenderer.drawInBatch(text, width, 0f,
                        0x67ff67, false, text_matrix, buffer, Font.DisplayMode.NORMAL,
                        (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.33F) * 255.0F) << 24,
                        combinedLight);
                ms.popPose();
            }
            if (!stack.isEmpty()) {
                ms.pushPose();
                ms.translate(0.5F, 1.46 + 0.03 * Math.cos(0.05f * time), 0.5F);
                ms.mulPose(Axis.XN.rotationDegrees(90f));
                ms.mulPose(Axis.XN.rotationDegrees((float) (3.75f * Math.sin(time / 12.5f))));
                ms.mulPose(Axis.ZN.rotationDegrees(-time / 0.325f));
                ms.scale(s, s, s);
                Minecraft.getInstance().getItemRenderer().
                        renderStatic(stack, ItemDisplayContext.FIXED, combinedLight,
                                combinedOverlay, ms, buffer, te.getLevel(), 0);
                ms.popPose();
            }
            if (!stack1.isEmpty()) {
                ms.pushPose();
                ms.translate(0.5F, 1.32 + 0.02 * Math.sin(0.03f * time), 0.5F);
                ms.mulPose(Axis.XN.rotationDegrees(90f));
                ms.mulPose(Axis.YN.rotationDegrees((float) (3.75f * Math.cos(time / 12.5f))));
                ms.mulPose(Axis.ZN.rotationDegrees(time / 0.625f));
                ms.scale(s, s, s);
                Minecraft.getInstance().getItemRenderer().
                        renderStatic(stack1, ItemDisplayContext.FIXED, combinedLight,
                                combinedOverlay, ms, buffer, te.getLevel(), 0);
                ms.popPose();
            }
            if (!stack2.isEmpty()) {
                ms.pushPose();
                ms.translate(0.5F, 1.23 + 0.02 * Math.sin(0.03f * time), 0.5F);
                ms.mulPose(Axis.XN.rotationDegrees(90f));
                ms.mulPose(Axis.YN.rotationDegrees((float) (-1.75f * Math.sin(time / 8.5f))));
                ms.mulPose(Axis.ZN.rotationDegrees(time / 1.125f));
                ms.scale(s, s, s);
                Minecraft.getInstance().getItemRenderer().
                        renderStatic(stack2, ItemDisplayContext.FIXED, combinedLight,
                                combinedOverlay, ms, buffer, te.getLevel(), 0);
                ms.popPose();
            }
        }
        {
            float[] angles = new float[9];
            float anglePer = 360F / 9;
            float totalAngle = 0F;
            for (short i = 0; i < angles.length; i++) {
                angles[i] = totalAngle += anglePer;
            }

            if (stack1.isEmpty() && !stack.isEmpty()) {
                for (short i = 0; i < 9; i++) {
                    ms.pushPose();
                    ms.translate(0.5F, .35F, 0.5F);
                    ms.mulPose(Axis.YP.rotationDegrees(-(angles[i] + (float) time) + 50));
                    ms.translate(0.925F, 0F, 0.25F);
                    ms.mulPose(Axis.YP.rotationDegrees(90F));
                    ItemStack render = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(
                            WizzMod.MODID, EnchantmentsHandler.GLYPHS_LIST.get(i))));
                    float s = 0.9f;
                    ms.scale(s, s, s);
                    if (i % 2 == 0)
                        ms.mulPose(Axis.ZP.rotationDegrees((float) (3.75f * Math.cos((te.getLevel().getGameTime() / 12.5f)))));
                    else
                        ms.mulPose(Axis.ZP.rotationDegrees((float) (-2.15f * Math.sin((te.getLevel().getGameTime() / 4.5f)))));
                    if (!stack.isEmpty()) {
                        if (T2_LIST.contains(Items.getResourceLocation(stack.getItem()).getPath() + "_" +
                                Items.getResourceLocation(render.getItem()).getPath()))
                            ms.translate(0, 0.45 + 0.075 * Math.cos(time / 8.2), 0);
                    }
                    Minecraft.getInstance().getItemRenderer().renderStatic(render,
                            ItemDisplayContext.GROUND,
                            combinedLight, combinedOverlay, ms, buffer, te.getLevel(), 0);
                    ms.popPose();
                }
            }
        }
        if(!stack.isEmpty() && stack2.isEmpty()) {
            short c = (short)T3_LIST.stream().filter(s1 -> s1.startsWith(
                    Items.getResourceLocation(stack.getItem()).getPath()
            )).toArray().length;
            if(c > 0){
                float[] angles = new float[c];
                float anglePer = 360F / c;
                float totalAngle = 0F;
                for (short j = 0; j < angles.length; j++) {
                    angles[j] = totalAngle += anglePer;
                }
                for (short i = 0; i < c; i++) {
                    ms.pushPose();
                    ms.translate(0.5F, .35F, 0.5F);
                    ms.mulPose(Axis.YP.rotationDegrees(-(angles[i] + time) + 50));
                    ms.translate(0.925F + (c * 0.025), 0F, 0.25F);
                    ms.mulPose(Axis.YP.rotationDegrees(90F));
                    String glyph2 = ((String)T3_LIST.stream().filter(string -> string.startsWith(
                            Items.getResourceLocation(stack.getItem()).getPath()
                    )).toArray()[i]).split("_")[1];
                    String glyph3 = ((String)T3_LIST.stream().filter(string -> string.startsWith(
                            Items.getResourceLocation(stack.getItem()).getPath()
                    )).toArray()[i]).split("_")[2];
                    ItemStack render = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(WizzMod.MODID, glyph2)));
                    ItemStack render1 = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(WizzMod.MODID, glyph3)));
                    float s = 0.9f;
                    ms.scale(s, s, s);
                    if (i % 2 == 0)
                        ms.mulPose(Axis.ZP.rotationDegrees((float) (3.75f * Math.cos((te.getLevel().getGameTime() / 12.5f)))));
                    else
                        ms.mulPose(Axis.ZP.rotationDegrees((float) (-2.15f * Math.sin((te.getLevel().getGameTime() / 4.5f)))));
                    ms.translate(0, 1.15 + 0.075 * Math.cos(time / 8.2), 0);
                    ms.pushPose();
                    ms.translate(0, 0.25, 0.15);
                    Minecraft.getInstance().getItemRenderer().renderStatic(render1,
                            ItemDisplayContext.GROUND,
                            combinedLight, combinedOverlay, ms, buffer, te.getLevel(), 0);
                    ms.popPose();
                    Minecraft.getInstance().getItemRenderer().renderStatic(render,
                            ItemDisplayContext.GROUND,
                            combinedLight, combinedOverlay, ms, buffer, te.getLevel(), 0);
                    ms.popPose();
                }
            }
        }
    }
    void renderGlyphs(WordForgeBlockEntity te, PoseStack ms, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ms.pushPose();
        ms.translate(0.5, 1.75, 0.5);
        ms.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        ms.translate(0.07 * te.handler.getSlots(), 0.15, 0);
        ms.scale(0.25f, 0.25f, 0.25f);
        for (int i = 0; i < te.handler.getSlots(); i++) {
            if(!te.handler.getStackInSlot(i).isEmpty())
                Minecraft.getInstance().getItemRenderer().renderStatic(te.handler.getStackInSlot(i),
                        ItemDisplayContext.FIXED, 15728880, combinedOverlay, ms, buffer, te.getLevel(), 0);
            //renderGlyph(te, ms, i);
            ms.translate(-1, 0, 0);
        }
        ms.popPose();
    }
    /*private void renderGlyph(WordMachineBlockEntity te, MatrixStack ms, int slot) {
        ms.pushPose();
        RenderSystem.disableLighting();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f,
                Math.abs(Math.sin(te.getLevel().getGameTime()/16f)));
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        ms.mulPose(Axis.YN.rotationDegrees(180f));
        ms.mulPose(Axis.XN.rotationDegrees(90f));
        Matrix4f mm = ms.last().pose();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        Minecraft.getInstance().getTextureManager().bind(getGlyphTexture(te, slot));
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(mm,0.5f, 0, 0.5f).uv(0, 0).endVertex();
        buffer.vertex(mm,-0.5f, 0, 0.5f).uv(1, 0).endVertex();
        buffer.vertex(mm,-0.5f, 0, -0.5f).uv(1, 1).endVertex();
        buffer.vertex(mm,0.5f, 0, -0.5f).uv(0, 1).endVertex();
        tessellator.end();
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableLighting();
        ms.popPose();
    }
    private ResourceLocation getGlyphTexture(WordMachineBlockEntity tile, int slot) {
        return new ResourceLocation(WizzMod.MODID,
                "textures/item/" + tile.handler.getStackInSlot(slot).getItem().getRegistryName().getPath()
                        + ".png");
    }*/
}
