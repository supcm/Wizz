package net.supcm.wizz.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.function.Function;

public class RenderingHelper {
    private static final Function<ResourceLocation, RenderType> TEXTURE = Util.memoize((texture) -> {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader))
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, true))
                .createCompositeState(true);
        return RenderType.create("texture",
                DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 131072,
                true, false, state);
    });
    public static void drawTexture(PoseStack ms, MultiBufferSource buffer, ResourceLocation texture,
                                   float r, float g, float b, float a) {
        ms.pushPose();
        Matrix4f mm = ms.last().pose();
        VertexConsumer tesselator = buffer.getBuffer(texutre(texture));
        AbstractTexture at = Minecraft.getInstance().getTextureManager().getTexture(texture);
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
}
