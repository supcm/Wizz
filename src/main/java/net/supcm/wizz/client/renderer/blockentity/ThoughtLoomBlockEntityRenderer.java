package net.supcm.wizz.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.supcm.wizz.common.block.entity.ThoughtLoomBlockEntity;
import org.joml.Matrix4f;

public class ThoughtLoomBlockEntityRenderer implements BlockEntityRenderer<ThoughtLoomBlockEntity> {
    float s = 0.55F;
    Minecraft mc = Minecraft.getInstance();
    public ThoughtLoomBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
    @Override
    public void render(ThoughtLoomBlockEntity te, float partialTicks, PoseStack ms,
                       MultiBufferSource buffer, int light, int overlay) {
        ItemStack stack = te.handler.getStackInSlot(0);
        ItemStack stack1 = te.handler.getStackInSlot(1);
        ItemStack stack2 = te.handler.getStackInSlot(2);
        ItemStack stack3 = te.handler.getStackInSlot(3);
        if(!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5, 1.25 + 0.015 * Math.cos(0.15f * partialTicks), 0.5);
            ms.mulPose(Axis.YP.rotation(partialTicks / 24.8525f));
            ms.scale(0.85f, 0.85f, 0.85f);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack, ItemDisplayContext.GROUND, light,
                            overlay, ms, buffer, te.getLevel(), 0);
            ms.popPose();
        }
        if(!stack1.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, .5F, 0.5F);
            ms.mulPose(Axis.YP.rotationDegrees((float)-(360 + 0.85 * partialTicks)));
            ms.translate(0.95F, 0F, 0.25F);
            ms.mulPose(Axis.YP.rotation(90f));
            ms.scale(0.65f, 0.65f, 0.65f);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack1, ItemDisplayContext.GROUND, light,
                            overlay, ms, buffer, te.getLevel(), 0);
            ms.popPose();
        }
        if(!stack2.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, .65F, 0.5F);
            ms.mulPose(Axis.YP.rotationDegrees((float)-(360 + 0.65 * partialTicks)));
            ms.translate(1.225F, 0F, 0.25F);
            ms.mulPose(Axis.YP.rotation(90f));
            ms.scale(0.65f, 0.65f, 0.65f);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack2, ItemDisplayContext.GROUND, light,
                            overlay, ms, buffer, te.getLevel(), 0);
            ms.popPose();
        }
        if(!stack3.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, .85F, 0.5F);
            ms.mulPose(Axis.YP.rotationDegrees((float)-(360 + 0.45 * partialTicks)));
            ms.translate(1.425F, 0F, 0.25F);
            ms.mulPose(Axis.YP.rotation(90f));
            ms.scale(0.65f, 0.65f, 0.65f);
            Minecraft.getInstance().getItemRenderer().
                    renderStatic(stack3, ItemDisplayContext.GROUND, light,
                            overlay, ms, buffer, te.getLevel(), 0);
            ms.popPose();
        }
    }
}
