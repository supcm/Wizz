package net.supcm.wizz.common.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.supcm.wizz.client.screen.MortarScreen;

public class MortarScreenPacket {
    public final BlockPos pos;
    public MortarScreenPacket(BlockPos pos) {
        this.pos = pos;
    }
    public static MortarScreenPacket load(FriendlyByteBuf buffer){
        return new MortarScreenPacket(buffer.readBlockPos());
    }
    public void save(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }
    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> openScreen(pos));
        ctx.setPacketHandled(true);
    }
    @OnlyIn(Dist.CLIENT)
    public static void openScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new MortarScreen(pos));
    }
}
