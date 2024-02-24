package net.supcm.wizz.client.renderer.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.client.RenderingHelper;
import net.supcm.wizz.common.block.entity.ReassessmentPillarBlockEntity;
import net.supcm.wizz.common.item.Items;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ReassessmentPillarBlockEntityRenderer implements BlockEntityRenderer<ReassessmentPillarBlockEntity> {
    private final ResourceLocation EFFECT_TEXTURE = new ResourceLocation(WizzMod.MODID,
            "textures/block/reassessment_circle.png");
    final float s = 0.55F;
    public ReassessmentPillarBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
    @Override public void render(ReassessmentPillarBlockEntity te, float partialTicks, PoseStack ms,
                                 MultiBufferSource buffer, int combinedLight, int combinedOverlay ) {
        ItemStack stack = te.handler.getStackInSlot(0);
        if(!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 1.25 + 0.035 * Math.cos(0.05f * te.getLevel().getGameTime()), 0.5F);
            ms.mulPose(Axis.YN.rotationDegrees(te.getLevel().getGameTime() / 0.8525f));
            ms.scale(s, s, s);
            ms.mulPose(Axis.XN.rotationDegrees((float) (3.75f * Math.sin(te.getLevel().getGameTime() / 12.5f))));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLight,
                    combinedOverlay, ms, buffer, te.getLevel(), 0);
            ms.popPose();
            if(hasRightConception(stack, te.concepts)){
                ms.pushPose();
                ms.translate(0.5, 1.05, 0.5);
                ms.mulPose(Axis.XN.rotationDegrees(180f));
                ms.scale(0.75f, 0.75f, 0.75f);
                renderEffect(ms, buffer,te,
                        ((Items.ConceptItem) stack.getItem()).getColor()[0],
                        ((Items.ConceptItem) stack.getItem()).getColor()[1],
                        ((Items.ConceptItem) stack.getItem()).getColor()[2]);
                ms.popPose();
            }
        }
    }
    boolean hasRightConception(ItemStack stack, List<Integer> concepts) {
        boolean hasRightItemStack = false;
        if(concepts != null){
            for (int i = 0; i < concepts.size(); i++) {
                if (concepts.get(i) != 0) {
                    if (i == 0) {
                        if (stack.getItem() == Items.CONCEPT_BEAUTY.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    } else if (i == 1) {
                        if (stack.getItem() == Items.CONCEPT_CREATION.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    } else if (i == 2) {
                        if (stack.getItem() == Items.CONCEPT_ART.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    } else if (i == 3) {
                        if (stack.getItem() == Items.CONCEPT_TRUTH.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    } else if (i == 4) {
                        if (stack.getItem() == Items.CONCEPT_SOUL.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    } else {
                        if (stack.getItem() == Items.CONCEPT_LIES.get()) {
                            hasRightItemStack = true;
                            break;
                        }
                    }
                }
            }
        }
        return hasRightItemStack;
    }
    public void renderEffect(PoseStack ms, MultiBufferSource buffer, ReassessmentPillarBlockEntity te,
                             float r, float g, float b) {
        ms.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
        ms.mulPose(Axis.YN.rotationDegrees(te.getLevel().getGameTime()));
        RenderingHelper.drawTexture(ms, buffer, EFFECT_TEXTURE, r, g, b, 1);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        ms.popPose();
    }
}
