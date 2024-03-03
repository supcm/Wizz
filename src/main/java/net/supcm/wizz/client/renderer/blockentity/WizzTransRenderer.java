package net.supcm.wizz.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.supcm.wizz.client.RenderingHelper;
import net.supcm.wizz.common.block.entity.WizzTransBlockEntity;

public class WizzTransRenderer /*implements BlockEntityRenderer<WizzTransBlockEntity>*/ {
    public WizzTransRenderer(BlockEntityRendererProvider.Context ctx) {

    }

    /*@Override public void render(WizzTransBlockEntity blockEntity, float partialTicks, PoseStack stack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if(blockEntity.vector != null) {
            //TODO: There must smth like: y = -(1/k)(x^2+z^2) + max_len + 1 - k; k - vector length, max_len - max length of vector (smth about 6-7)
            float time = blockEntity.getLevel().getGameTime() + partialTicks;
            RenderingHelper.drawWizz(stack, buffer, time, (float) (blockEntity.vector.x + 0.5f),
                    (float) (blockEntity.vector.y + 0.5f), (float) (blockEntity.vector.z + 0.5f));
        }
    }*/
}
