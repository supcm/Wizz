package net.supcm.wizz.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.supcm.wizz.common.block.entity.WordEraserBlockEntity;
import org.joml.Matrix4f;

public class WordEraserBlockEntityRenderer implements BlockEntityRenderer<WordEraserBlockEntity> {
    final float s = 0.55F;
    public WordEraserBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(WordEraserBlockEntity te, float partialTicks, PoseStack ms,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = te.handler.getStackInSlot(0);
        if (!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 0.4 + 0.035 * Math.cos(0.05f * partialTicks), 0.5F);
            ms.mulPose(Axis.YN.rotationDegrees(partialTicks / 0.8525f));
            ms.scale(s, s, s);
            ms.mulPose(Axis.XN.rotationDegrees((float) (3.75 * Math.sin(partialTicks / 12.5))));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLight,
                    combinedOverlay, ms, buffer, te.getLevel(), 0);
            ms.popPose();
            if (!te.currentEnchantmentName.equals("")) {
                ms.pushPose();
                ms.translate(0.5, 1.45, 0.5);
                ms.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
                ms.scale(-0.025f, -0.025f, 0.025f);
                Font fontrenderer = Minecraft.getInstance().font;
                Component text = Component.translatable(te.currentEnchantmentName);
                float width = (float) (-fontrenderer.width(text.getString()) / 2);
                Matrix4f text_matrix = ms.last().pose();
                fontrenderer.drawInBatch(text, width, 0f,
                        0x67ff67, false, text_matrix, buffer, Font.DisplayMode.NORMAL,
                        (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.33F) * 255.0F) << 24,
                        combinedLight);
                ms.popPose();
            }
        }

    }
}