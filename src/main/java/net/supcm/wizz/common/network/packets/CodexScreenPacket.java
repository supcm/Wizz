package net.supcm.wizz.common.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.supcm.wizz.client.screen.CodexScreen;

import java.util.function.Supplier;

public class CodexScreenPacket {
    public final CompoundTag tag;
    public CodexScreenPacket(CompoundTag tag) { this.tag = tag;  }
    public static CodexScreenPacket load(FriendlyByteBuf buffer){
        return new CodexScreenPacket(buffer.readNbt());
    }
    public void save(FriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> openScreen(tag));
        ctx.get().setPacketHandled(true);
    }
    @OnlyIn(Dist.CLIENT)
    public static void openScreen(CompoundTag msg) {
        Minecraft.getInstance().setScreen(new CodexScreen(msg.getList("Revealed", 8)));
    }
}
