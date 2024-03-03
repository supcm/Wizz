package net.supcm.wizz.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.supcm.wizz.common.block.entity.MatrixBlockEntity;
import net.supcm.wizz.common.item.Items;

public class MatrixBlockEntityRenderer implements BlockEntityRenderer<MatrixBlockEntity> {
    public MatrixBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
    @Override
    public void render(MatrixBlockEntity tile, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
        float time = tile.getLevel().getGameTime() + partialTicks;
        if(!tile.handler.getStackInSlot(1).isEmpty()) {
            ms.pushPose();
            renderSymbol(tile, ms, 0.045f, 0.5f,true, combinedLight, combinedOverlay, buffer);
            renderSymbol(tile, ms, 0.5f, 0.045f,false, combinedLight, combinedOverlay, buffer);
            renderSymbol(tile, ms, 1-0.045f, 0.5f,true, combinedLight, combinedOverlay, buffer);
            renderSymbol(tile, ms, 0.5f, 1-0.045f,false, combinedLight, combinedOverlay, buffer);
            ms.popPose();
        }
        if(!tile.handler.getStackInSlot(0).isEmpty()) {
            ItemStack stack = tile.handler.getStackInSlot(0);
            ms.pushPose();
            ms.translate(0.5, 1.16 + 0.02 * Math.sin(0.03f*partialTicks), 0.5);
            ms.mulPose(Axis.XN.rotationDegrees(90f));
            ms.mulPose(Axis.ZN.rotationDegrees(time/0.625123f));
            ms.scale(0.45f, 0.45f, 0.45f);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack, ItemDisplayContext.FIXED, combinedLight,
                            combinedOverlay, ms, buffer, tile.getLevel(), 0);
            ms.popPose();
            if(tile.doRenderCrystal) {
                ItemStack crystal = new ItemStack(Items.LAVA_CRYSTAL.get());
                ms.pushPose();
                ms.translate(0.5, 1.35-((tile.renderTick/3.125)* 0.01), 0.5);
                ms.mulPose(Axis.XN.rotationDegrees(90f));
                ms.mulPose(Axis.ZN.rotationDegrees(time/0.625123f));
                ms.scale(0.3f, 0.3f, 0.3f);
                Minecraft.getInstance().getItemRenderer().
                        renderStatic(crystal, ItemDisplayContext.FIXED, combinedLight,
                                combinedOverlay, ms, buffer, tile.getLevel(), 0);
                ms.popPose();
            }
        }
    }
    public void renderSymbol(MatrixBlockEntity te, PoseStack ms, float x, float z, boolean rotate,
                             int combinedLight, int combinedOverlay, MultiBufferSource buffer) {
        ItemStack stack = te.handler.getStackInSlot(1);
        ms.pushPose();
        ms.translate(x, 0.375, z);
        if(rotate) ms.mulPose(Axis.YP.rotationDegrees(90));
        float s = 0.33f;
        ms.scale(s, s, s);
        Minecraft.getInstance().getItemRenderer().
                renderStatic(stack, ItemDisplayContext.FIXED, combinedLight,
                        combinedOverlay, ms, buffer, te.getLevel(), 0);
        ms.popPose();
    }
}
