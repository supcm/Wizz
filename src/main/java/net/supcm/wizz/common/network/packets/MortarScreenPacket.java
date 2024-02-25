package net.supcm.wizz.common.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.supcm.wizz.client.screen.MortarScreen;

import java.util.function.Supplier;

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
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> openScreen(pos));
        ctx.get().setPacketHandled(true);
    }
    @OnlyIn(Dist.CLIENT)
    public static void openScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new MortarScreen(pos));
    }
}
