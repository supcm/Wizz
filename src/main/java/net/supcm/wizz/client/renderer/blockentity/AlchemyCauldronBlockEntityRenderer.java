package net.supcm.wizz.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.block.entity.AlchemyCauldronBlockEntity;
import org.joml.Matrix4f;

public class AlchemyCauldronBlockEntityRenderer implements BlockEntityRenderer<AlchemyCauldronBlockEntity> {
    static Minecraft minecraft = Minecraft.getInstance();
    public AlchemyCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {

    }
    @Override public void render(AlchemyCauldronBlockEntity tile, float partialTicks, PoseStack stack,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if(tile.getSeconds() > 0) {
            stack.clear();
            stack.pushPose();
            stack.translate(0.5, 1.5, 0.5);
            stack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
            stack.scale(-0.025f, -0.025f, 0.025f);
            Font fontrenderer = minecraft.font;
            String text = String.valueOf(tile.getSeconds());
            float width = (float) (-fontrenderer.width(text) / 2);
            Matrix4f text_matrix = stack.last().pose();
            fontrenderer.drawInBatch(text, width, 0f,
                    0xFFFFFF, false, text_matrix, buffer, Font.DisplayMode.NORMAL,
                    ((int)(minecraft.options.getBackgroundOpacity(0.33F) * 255)) << 24,
                    combinedLight);
            stack.popPose();
        }
    }
}
