package net.supcm.wizz.common.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.supcm.wizz.client.renderer.blockentity.WordForgeBlockEntityRenderer;

import java.util.ArrayList;
import java.util.List;

public class T3ListPacket {
    public final List<String> list;
    public T3ListPacket(List<String> list) { this.list = list; }
    public static T3ListPacket load(FriendlyByteBuf buffer){
        List<String> list = new ArrayList<>();
        int l = buffer.readVarInt();
        for(int i = 0; i < l; i++)
            list.add(buffer.readUtf(11));
        return new T3ListPacket(list);
    }
    public void save(FriendlyByteBuf buffer) {
        buffer.writeVarInt(list.size());
        for(String str : list)
            buffer.writeUtf(str, 11);
    }
    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> WordForgeBlockEntityRenderer.setListT3(list));
        ctx.setPacketHandled(true);
    }
}
