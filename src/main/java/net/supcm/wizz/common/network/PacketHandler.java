package net.supcm.wizz.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.network.packets.*;

public class PacketHandler {

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(WizzMod.MODID,"main"))
            .serverAcceptedVersions((version) -> true)
            .clientAcceptedVersions((version) -> true)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    public static void registerPackets() {
        int id = 0;
        CHANNEL.messageBuilder(T2ListPacket.class, id++)
                .encoder(T2ListPacket::save).decoder(T2ListPacket::load).consumerMainThread(T2ListPacket::handle).add();
        CHANNEL.messageBuilder(T3ListPacket.class, id++)
                .encoder(T3ListPacket::save).decoder(T3ListPacket::load).consumerMainThread(T3ListPacket::handle).add();
        CHANNEL.messageBuilder(CodexScreenPacket.class, id++)
                .encoder(CodexScreenPacket::save).decoder(CodexScreenPacket::load).consumerMainThread(CodexScreenPacket::handle).add();
        CHANNEL.messageBuilder(MortarScreenPacket.class, id++)
                .encoder(MortarScreenPacket::save).decoder(MortarScreenPacket::load).consumerMainThread(MortarScreenPacket::handle).add();
        CHANNEL.messageBuilder(MortarRecipePacket.class, id++)
                .encoder(MortarRecipePacket::save).decoder(MortarRecipePacket::load).consumerMainThread(MortarRecipePacket::handle).add();
    }
}
