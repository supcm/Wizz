package net.supcm.wizz.common.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.supcm.wizz.client.renderer.blockentity.WordForgeBlockEntityRenderer;
import net.supcm.wizz.client.renderer.blockentity.WordMachineBlockEntityRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class T2ListPacket {
    public final List<String> list;
    public T2ListPacket(List<String> list) { this.list = list; }
    public static T2ListPacket load(FriendlyByteBuf buffer){
        List<String> list = new ArrayList<>();
        int l = buffer.readVarInt();
        for(int i = 0; i < l; i++)
            list.add(buffer.readUtf(7));
        return new T2ListPacket(list);
    }
    public void save(FriendlyByteBuf buffer) {
        buffer.writeVarInt(list.size());
        for(String str : list)
            buffer.writeUtf(str, 7);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            WordMachineBlockEntityRenderer.setList(list);
            WordForgeBlockEntityRenderer.setListT2(list);
        });
        ctx.get().setPacketHandled(true);
    }
}
