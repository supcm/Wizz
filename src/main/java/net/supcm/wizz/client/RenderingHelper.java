package net.supcm.wizz.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.item.CatsEyeItem;
import org.joml.Matrix4f;

import java.util.function.Function;

public class RenderingHelper {
    private static final ResourceLocation REASSESSMENT_CIRCLE = new ResourceLocation(WizzMod.MODID,
            "textures/block/reassessment_circle.png");
    private static final Function<ResourceLocation, RenderType> TEXTURE = Util.memoize((texture) -> {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader))
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, true))
                .createCompositeState(true);
        return RenderType.create("texture",
                DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 131072,
                true, false, state);
    });
    public static void drawReassessmentTexture(PoseStack ms, MultiBufferSource buffer,
                                   float r, float g, float b, float a) {
        ms.pushPose();
        Matrix4f mm = ms.last().pose();
        VertexConsumer tesselator = buffer.getBuffer(texutre(REASSESSMENT_CIRCLE));
        AbstractTexture at = Minecraft.getInstance().getTextureManager().getTexture(REASSESSMENT_CIRCLE);
        at.bind();
        tesselator.vertex(mm,0.5f, 0, 0.5f).uv(0, 0).color(r, g, b, a)
                .endVertex();
        tesselator.vertex(mm,-0.5f, 0, 0.5f).uv(1, 0).color(r, g, b, a)
                .endVertex();
        tesselator.vertex(mm,-0.5f, 0, -0.5f).uv(1, 1).color(r, g, b, a)
                .endVertex();
        tesselator.vertex(mm,0.5f, 0, -0.5f).uv(0, 1).color(r, g, b, a)
                .endVertex();
        at.close();
        ms.popPose();
    }
    private static RenderType texutre(ResourceLocation texture) {
        return TEXTURE.apply(texture);
    }
    public static void renderStillWater(Level level, BlockPos pos, int tint_mod, MultiBufferSource buffer,
                                   PoseStack stack, int combinedLight) {
        IClientFluidTypeExtensions fluid = IClientFluidTypeExtensions.of(Fluids.WATER);
        int tint = fluid.getTintColor(Fluids.WATER.defaultFluidState(), level, pos)
                << tint_mod;
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(fluid.getStillTexture());
        VertexConsumer builder =
                buffer.getBuffer(ItemBlockRenderTypes.getRenderLayer(Fluids.WATER.defaultFluidState()));
        stack.pushPose();
        drawQuad(builder, stack, 0.1f, 0.945f, 0.1f, 0.9f,
                0.945f, 0.9f, texture.getU0(), texture.getV0(), texture.getU1(), texture.getV1(),
                combinedLight, tint);
        stack.popPose();
    }
    private static void drawVertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float z, float u,
                                   float v, int packedLight, int color) {
        builder.vertex(poseStack.last().pose(), x, y, z)
                .color(color)
                .uv(u, v)
                .uv2(packedLight)
                .normal(1, 0, 0)
                .endVertex();
    }

    private static void drawQuad(VertexConsumer builder, PoseStack poseStack, float x0, float y0, float z0, float x1,
                                 float y1, float z1, float u0, float v0, float u1, float v1,
                                 int packedLight, int color) {
        drawVertex(builder, poseStack, x0, y0, z0, u0, v0, packedLight, color);
        drawVertex(builder, poseStack, x0, y1, z1, u0, v1, packedLight, color);
        drawVertex(builder, poseStack, x1, y1, z1, u1, v1, packedLight, color);
        drawVertex(builder, poseStack, x1, y0, z0, u1, v0, packedLight, color);
    }

    public static void drawWizz(PoseStack stack, MultiBufferSource buffer, float time,
                                float x, float y, float z) {
        if(canSeeWizz()) {
            stack.pushPose();
            VertexConsumer builder = buffer.getBuffer(RenderType.LINES);
            builder.vertex(stack.last().pose(), 0.5f, 0.5f + 0.25f * Mth.sin(time / 12.5f), 0.5f)
                    .color(0.9f, 0.5f, 0.5f, 1)
                    .normal(x > 0 ? 1 : -1, 0, z > 0 ? 1 : -1)
                    .endVertex();
            builder.vertex(stack.last().pose(), x, y + 0.25f * Mth.cos(time / 25), z)
                    .color(0.5f, 0.5f, 0.9f, 1)
                    .normal(x > 0 ? -1 : 1, 0, z > 0 ? -1 : 1)
                    .endVertex();
            stack.popPose();
        }
    }
    private static boolean canSeeWizz() {
        return Minecraft.getInstance().player.getInventory().items.stream().anyMatch(stack -> stack.getItem() instanceof CatsEyeItem);
    }
}
