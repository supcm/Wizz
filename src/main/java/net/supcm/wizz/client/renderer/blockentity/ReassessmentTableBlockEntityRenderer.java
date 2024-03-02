package net.supcm.wizz.client.renderer.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.block.entity.ReassessmentTableBlockEntity;
import org.lwjgl.opengl.GL11;

public class ReassessmentTableBlockEntityRenderer implements BlockEntityRenderer<ReassessmentTableBlockEntity> {
    private final ResourceLocation EFFECT_TEXTURE = new ResourceLocation(WizzMod.MODID,
            "textures/block/reassessment_circle.png");
    public ReassessmentTableBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
    @Override public void render(ReassessmentTableBlockEntity te, float partialTicks, PoseStack ms,
                                 MultiBufferSource buffer, int combinedLight, int combinedOverlay ) {
        ms.pushPose();
        ms.translate(0.5, 1, 0.5);
        ms.mulPose(Axis.XN.rotationDegrees(90f));
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Blocks.GLASS_PANE),
                ItemDisplayContext.FIXED, combinedLight,
                combinedOverlay, ms, buffer, te.getLevel(), 0);
        ms.popPose();
        boolean isValid = te.isValid;
        if(isValid) {
            ItemStack stack = te.handler.getStackInSlot(0);
            if (!stack.isEmpty()) {
                ms.pushPose();
                ms.translate(0.5F, 0.95 + 0.015 * Math.cos(0.05f * partialTicks), 0.5F);
                ms.mulPose(Axis.XN.rotationDegrees(90f));
                ms.mulPose(Axis.XN.rotationDegrees((float) (3.75f * Math.sin(partialTicks / 11.5f))));
                ms.mulPose(Axis.ZN.rotationDegrees(partialTicks / 1.8525f));
                ms.scale(0.55f, 0.55f, 0.55f);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLight,
                        combinedOverlay, ms, buffer, te.getLevel(), 0);
                ms.popPose();
            }
        } else renderInvalid(ms, te, buffer);
    }
    public void renderInvalid(PoseStack ms, ReassessmentTableBlockEntity tile, MultiBufferSource buffer) {
        ms.pushPose();
        BlockPos pos = tile.getBlockPos();
        for(int i = -2; i < 3; i++) {
            for(int j = -2; j < 3; j++) {
                if((j == -2 || j == 2) && i == 0) {
                    if(tile.getLevel().getBlockState(pos.south(j))
                            != net.supcm.wizz.common.block.Blocks.REASSESSMENT_PILLAR.get().defaultBlockState()) {
                        ms.pushPose();
                        ms.translate(i, 0, j);
                        if (tile.getLevel().getBlockState(pos.south(j)).isAir())
                            renderInvalidPillar(ms, buffer);
                        else
                            renderInvalidBlock(ms, buffer);
                        ms.popPose();
                    }
                } else if((j == 1 || j == -1) && (i == -2 || i == 2)) {
                    if(tile.getLevel().getBlockState(pos.south(j).east(i))
                            != net.supcm.wizz.common.block.Blocks.REASSESSMENT_PILLAR.get().defaultBlockState()) {
                        ms.pushPose();
                        ms.translate(i, 0, j);
                        if (tile.getLevel().getBlockState(pos.south(j).east(i)).isAir())
                            renderInvalidPillar(ms, buffer);
                        else
                            renderInvalidBlock(ms, buffer);
                        ms.popPose();
                    }
                } else if(!(i == 0 && j == 0)){
                    if(!tile.getLevel().getBlockState(pos.south(j).east(i)).isAir()){
                        ms.pushPose();
                        ms.translate(i, 0, j);
                        renderInvalidBlock(ms, buffer);
                        ms.popPose();
                    }
                }
            }
        }
        ms.popPose();
    }
    private void renderInvalidPillar(PoseStack ms, MultiBufferSource buffers) {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        ms.pushPose();
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(net.supcm.wizz.common.block.Blocks.REASSESSMENT_PILLAR.get().defaultBlockState(),
                ms, buffers, 0xAAFFAA, OverlayTexture.RED_OVERLAY_V, ModelData.EMPTY, RenderType.debugQuads());
        ms.popPose();
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }
    private void renderInvalidBlock(PoseStack ms, MultiBufferSource buffers) {
        ms.pushPose();
        RenderSystem.lineWidth(10);
        VertexConsumer builder = buffers.getBuffer(RenderType.LINES);
        LevelRenderer.renderLineBox(ms, builder,
                0, 0, 0, 1, 1, 1,
                1, 0, 0, 1, 1, 0 ,0);

        ms.popPose();
    }
    @Override public boolean shouldRenderOffScreen(ReassessmentTableBlockEntity tile) { return true; }
}
