package net.supcm.wizz.common.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.supcm.wizz.client.screen.CodexScreen;

public class CodexScreenPacket {
    public final CompoundTag tag;
    public CodexScreenPacket(CompoundTag tag) { this.tag = tag;  }
    public static CodexScreenPacket load(FriendlyByteBuf buffer){
        return new CodexScreenPacket(buffer.readNbt());
    }
    public void save(FriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }
    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> openScreen(tag));
        ctx.setPacketHandled(true);
    }
    @OnlyIn(Dist.CLIENT)
    public static void openScreen(CompoundTag msg) {
        Minecraft.getInstance().setScreen(new CodexScreen(msg.getList("Revealed", 8)));
    }
}
