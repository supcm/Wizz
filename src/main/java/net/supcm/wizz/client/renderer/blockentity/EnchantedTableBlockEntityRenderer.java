package net.supcm.wizz.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.supcm.wizz.common.block.entity.EnchantedTableBlockEntity;
import org.joml.Matrix4f;

public class EnchantedTableBlockEntityRenderer implements BlockEntityRenderer<EnchantedTableBlockEntity> {
    float s = 0.55F;
    Minecraft mc = Minecraft.getInstance();
    public EnchantedTableBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
    @Override
    public void render(EnchantedTableBlockEntity te, float partialTicks, PoseStack ms,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = te.handler.getStackInSlot(0);
        float time = te.getLevel().getGameTime() + partialTicks;
        if (!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 0.75 + 0.085 * Math.cos(0.05f * time), 0.5F);
            ms.mulPose(Axis.XN.rotationDegrees(90f));
            ms.mulPose(Axis.XN.rotationDegrees((float) (3.75f * Math.sin(time / 12.5f))));
            ms.mulPose(Axis.ZN.rotationDegrees(time / 1.8525f));
            ms.scale(s, s, s);
            mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLight,
                    combinedOverlay, ms, buffer, te.getLevel(), 0);
            ms.popPose();
            if (te.enchLevel != -1) {
                ms.clear();
                ms.pushPose();
                ms.translate(0.5, 1.5, 0.5);
                ms.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
                ms.scale(-0.025f, -0.025f, 0.025f);
                Font fontrenderer = mc.font;
                String text = String.valueOf(te.enchLevel);
                float width = (float) (-fontrenderer.width(text) / 2);
                Matrix4f text_matrix = ms.last().pose();
                fontrenderer.drawInBatch(text, width, 0f,
                        0x67ff67, false, text_matrix, buffer, Font.DisplayMode.NORMAL,
                        ((int)(mc.options.getBackgroundOpacity(0.33F) * 255)) << 24,
                        combinedLight);
                ms.popPose();
            }
            renderGlyphs(te, ms, buffer, combinedLight, combinedOverlay);
        }
    }

    void renderGlyphs(EnchantedTableBlockEntity te, PoseStack ms, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ms.pushPose();
        ms.translate(0.5, 1.55, 0.5);
        ms.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        ms.translate(0, 0.15, 0);
        ms.scale(0.25f, 0.25f, 0.25f);
        for (int i = 0; i < te.handler.getSlots(); i++) {
            if(!te.handler.getStackInSlot(i).isEmpty())
                mc.getItemRenderer().renderStatic(te.handler.getStackInSlot(i),
                        ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, ms, buffer, te.getLevel(), 0);
            //renderGlyph(te, ms, i);
            ms.translate(-1, 0, 0);
        }
        ms.popPose();
    }
    /*private void renderGlyph(EnchantedTableBlockEntity te, PoseStack ms, int slot) {
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
    private ResourceLocation getGlyphTexture(EnchantedTableBlockEntity tile, int slot) {
        return new ResourceLocation(WizzMod.MODID,
                "textures/item/" + Items.getResourceLocation(tile.handler.getStackInSlot(slot).getItem()).getPath()
                        + ".png");
    }*/
}
