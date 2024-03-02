package net.supcm.wizz.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.supcm.wizz.common.block.entity.EnchantingStationBlockEntity;
import net.supcm.wizz.common.item.Items;

public class EnchantingStationBlockEntityRenderer implements BlockEntityRenderer<EnchantingStationBlockEntity> {
    float s = 0.55F;
    Minecraft mc = Minecraft.getInstance();
    public EnchantingStationBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
    @Override
    public void render(EnchantingStationBlockEntity te, float partialTicks, PoseStack ms,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        renderCrystal(te, ms, 0.25f, 0.25f, combinedLight, combinedOverlay, buffer);
        renderCrystal(te, ms, 1-0.25f, 0.25f, combinedLight, combinedOverlay, buffer);
        renderCrystal(te, ms, 0.25f, 1-0.25f, combinedLight, combinedOverlay, buffer);
        renderCrystal(te, ms, 1-0.25f, 1-0.25f, combinedLight, combinedOverlay, buffer);
        ItemStack stack = te.handler.getStackInSlot(0);
        ItemStack stack1 = te.handler.getStackInSlot(1);
        if(!stack.isEmpty()){
            ms.pushPose();
            ms.translate(0.5F, 1.15 + 0.025 * Math.cos(0.05f * partialTicks), 0.5F);
            ms.mulPose(Axis.XN.rotationDegrees(90f));
            ms.mulPose(Axis.XN.rotationDegrees((float) (3.75f * Math.cos(partialTicks / 8.5f))));
            ms.mulPose(Axis.ZN.rotationDegrees(partialTicks / -0.9525f));
            float s = 0.55f;
            ms.scale(s, s, s);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack,
                    ItemDisplayContext.FIXED, combinedLight, combinedOverlay, ms, buffer, te.getLevel(), 0);
            ms.popPose();
        }
        if(!stack1.isEmpty() || te.tick != 0) {
            float[] angles = new float[stack1.getCount()];
            float anglePer = 360F / stack1.getCount();
            float totalAngle = 0F;
            for (int i = 0; i < angles.length; i++) {
                angles[i] = totalAngle += anglePer;
            }
            for (int i = 0; i < stack1.getCount(); i++) {
                ms.pushPose();
                ms.translate(0.5F, 0.95F, 0.5F);
                if(te.doCraft) {
                    ms.mulPose(Axis.YP.rotationDegrees(-(angles[i] + partialTicks)+20*(te.tick*0.12525f)));
                    ms.translate(1.45F-(te.tick*0.012525f), 0F, 0.25F);
                } else {
                    ms.mulPose(Axis.YP.rotationDegrees(-(angles[i] + partialTicks) + 50));
                    ms.translate(1.45F, 0F, 0.25F);
                }
                ms.mulPose(Axis.YP.rotationDegrees(90F));;
                float s = 0.9f;
                ms.scale(s, s, s);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack1,
                        ItemDisplayContext.GROUND, combinedLight, combinedOverlay, ms, buffer, te.getLevel(), 0);
                ms.popPose();
            }
        }
    }

    public void renderCrystal(EnchantingStationBlockEntity te, PoseStack ms, float x, float z, int combinedLight, int combinedOverlay,
                              MultiBufferSource buffer) {
        ItemStack stack = new ItemStack(Items.CRYSTAL.get());
        ms.pushPose();
        ms.translate(x, 0.25 + 0.015 * Math.cos(0.05f * te.getLevel().getGameTime()), z);
        ms.mulPose(Axis.YP.rotationDegrees(te.getLevel().getGameTime() / 2.8525f));
        float s = 0.5f;
        ms.scale(s, s, s);
        Minecraft.getInstance().getItemRenderer().
                renderStatic(stack, ItemDisplayContext.GROUND, combinedLight,
                        combinedOverlay, ms, buffer, te.getLevel(), 0);
        ms.popPose();
    }
}
