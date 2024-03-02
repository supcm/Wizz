package net.supcm.wizz.client.renderer.blockentity;

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
import net.supcm.wizz.common.block.entity.WordMachineBlockEntity;
import net.supcm.wizz.common.handler.EnchantmentsHandler;
import net.supcm.wizz.common.item.Items;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class WordMachineBlockEntityRenderer implements BlockEntityRenderer<WordMachineBlockEntity> {
    float s = 0.55F;
    private static List<String> T2_LIST = new ArrayList<>();
    public static void setList(List<String> list) { T2_LIST = list; }
    public WordMachineBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
    @Override public void render(WordMachineBlockEntity te, float partialTicks, PoseStack ms,
                                 MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = te.handler.getStackInSlot(0);
        ItemStack stack1 = te.handler.getStackInSlot(1);
        float[] angles = new float[9];
        float anglePer = 360F / 9;
        float totalAngle = 0F;
        for (short i = 0; i < angles.length; i++) {
            angles[i] = totalAngle += anglePer;
        }
        renderGlyphs(te, ms, buffer, combinedLight, combinedOverlay);
        if(te.enchLevel != -1 && (!stack.isEmpty() || !stack1.isEmpty())) {
            ms.pushPose();
            ms.translate(0.5, 1.65, 0.5);
            ms.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            ms.scale(-0.025f, -0.025f, 0.025f);
            Font fontrenderer = Minecraft.getInstance().font;
            String text = String.valueOf(te.enchLevel);
            float width = (float)(-fontrenderer.width(text) / 2);
            Matrix4f text_matrix = ms.last().pose();
            fontrenderer.drawInBatch(text, width, 0f,
                    0x67ff67, false, text_matrix, buffer, Font.DisplayMode.NORMAL,
                    (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.33F) * 255.0F) << 24,
                    combinedLight);
            ms.popPose();
        }
        if(!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 1.36+0.03*Math.cos(0.05f * partialTicks), 0.5F);
            ms.mulPose(Axis.XN.rotationDegrees(90f));
            ms.mulPose(Axis.XN.rotationDegrees((float) (3.75f*Math.sin(partialTicks / 12.5f))));
            ms.mulPose(Axis.ZN.rotationDegrees(-partialTicks / 1.125f));
            float s = 0.55F;
            ms.scale(s, s, s);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack, ItemDisplayContext.FIXED, combinedLight,
                            combinedOverlay, ms, buffer, te.getLevel(), 0);
            ms.popPose();
        }
        if(!stack1.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 1.23 + 0.02 * Math.sin(0.03f * partialTicks), 0.5F);
            ms.mulPose(Axis.XN.rotationDegrees(90f));
            ms.mulPose(Axis.YN.rotationDegrees((float) (3.75f*Math.cos(partialTicks / 12.5f))));
            ms.mulPose(Axis.ZN.rotationDegrees(partialTicks / 2.825f));
            float s = 0.55F;
            ms.scale(s, s, s);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack1, ItemDisplayContext.FIXED, combinedLight,
                            combinedOverlay, ms, buffer, te.getLevel(), 0);
            ms.popPose();
        }
        if(stack1.isEmpty() && !stack.isEmpty()){
            for (short i = 0; i < 9; i++) {
                ms.pushPose();
                ms.translate(0.5F, .5F, 0.5F);
                ms.mulPose(Axis.YP.rotationDegrees(-(angles[i] + partialTicks) + 50));
                ms.translate(0.825F, 0F, 0.25F);
                ms.mulPose(Axis.YP.rotationDegrees(90F));
                ItemStack render = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(
                        WizzMod.MODID, EnchantmentsHandler.GLYPHS_LIST.get(i))));
                float s = 0.9f;
                ms.scale(s, s, s);
                if(i % 2 == 0)
                    ms.mulPose(Axis.ZP.rotationDegrees((float) (3.75f*Math.cos(partialTicks / 12.5f))));
                else
                    ms.mulPose(Axis.XP.rotationDegrees((float) (2.15f*Math.sin(partialTicks / 4.5f))));
                if (T2_LIST.contains(Items.getResourceLocation(stack.getItem()).getPath() + "_" +
                        Items.getResourceLocation(render.getItem()).getPath()))
                    ms.translate(0, 1.05 + 0.075 * Math.cos(partialTicks / 8.2), 0);
                Minecraft.getInstance().getItemRenderer().renderStatic(render,
                        ItemDisplayContext.GROUND, combinedLight, combinedOverlay, ms, buffer, te.getLevel(), 0);
                ms.popPose();
            }
        }
    }
    void renderGlyphs(WordMachineBlockEntity te, PoseStack ms, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
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
                MathHelper.abs(MathHelper.sin(te.getLevel().getGameTime()/16f)));
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        ms.mulPose(Vector3f.YN.rotationDegrees(180f));
        ms.mulPose(Vector3f.XN.rotationDegrees(90f));
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
