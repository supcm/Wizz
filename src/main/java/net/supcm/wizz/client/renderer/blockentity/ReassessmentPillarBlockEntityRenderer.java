package net.supcm.wizz.client.renderer.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.supcm.wizz.client.RenderingHelper;
import net.supcm.wizz.common.block.entity.ReassessmentPillarBlockEntity;
import net.supcm.wizz.common.item.Items;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;

public class ReassessmentPillarBlockEntityRenderer implements BlockEntityRenderer<ReassessmentPillarBlockEntity> {

    private static final HashMap<Item, Integer> CONCEPTS_IDS = new HashMap<>(6);
    static {
        int id = 0;
        CONCEPTS_IDS.put(Items.CONCEPT_BEAUTY.get(), id++);
        CONCEPTS_IDS.put(Items.CONCEPT_CREATION.get(), id++);
        CONCEPTS_IDS.put(Items.CONCEPT_ART.get(), id++);
        CONCEPTS_IDS.put(Items.CONCEPT_TRUTH.get(), id++);
        CONCEPTS_IDS.put(Items.CONCEPT_SOUL.get(), id++);
        CONCEPTS_IDS.put(Items.CONCEPT_LIES.get(), id);
    }
    final float s = 0.55F;
    public ReassessmentPillarBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
    @Override public void render(ReassessmentPillarBlockEntity te, float partialTicks, PoseStack ms,
                                 MultiBufferSource buffer, int combinedLight, int combinedOverlay ) {
        ItemStack stack = te.handler.getStackInSlot(0);
        if(!stack.isEmpty()) {
            ms.pushPose();
            ms.translate(0.5F, 1.25 + 0.035 * Math.cos(0.05f * partialTicks), 0.5F);
            ms.mulPose(Axis.YN.rotationDegrees(partialTicks / 0.8525f));
            ms.scale(s, s, s);
            ms.mulPose(Axis.XN.rotationDegrees((float) (3.75f * Math.sin(partialTicks / 12.5f))));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLight,
                    combinedOverlay, ms, buffer, te.getLevel(), 0);
            ms.popPose();
            if(hasRightConception(stack, te.concepts)){
                ms.pushPose();
                ms.translate(0.5, 1.05, 0.5);
                ms.mulPose(Axis.XN.rotationDegrees(180f));
                ms.scale(0.75f, 0.75f, 0.75f);
                renderEffect(ms, buffer,partialTicks,
                        ((Items.ConceptItem) stack.getItem()).getColor()[0],
                        ((Items.ConceptItem) stack.getItem()).getColor()[1],
                        ((Items.ConceptItem) stack.getItem()).getColor()[2]);
                ms.popPose();
            }
        }
    }
    boolean hasRightConception(ItemStack stack, List<Integer> concepts) {
        return concepts != null && concepts.get(CONCEPTS_IDS.get(stack.getItem())) != 0;
    }
    public void renderEffect(PoseStack ms, MultiBufferSource buffer, float partialTicks,
                             float r, float g, float b) {
        ms.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
        ms.mulPose(Axis.YN.rotationDegrees(partialTicks));
        RenderingHelper.drawReassessmentTexture(ms, buffer, r, g, b, 1);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        ms.popPose();
    }
}
