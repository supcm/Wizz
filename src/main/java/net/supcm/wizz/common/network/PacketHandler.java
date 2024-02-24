package net.supcm.wizz.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;
import net.supcm.wizz.WizzMod;
import net.supcm.wizz.common.network.packets.*;

public class PacketHandler {

    public static final SimpleChannel CHANNEL = ChannelBuilder.named(new ResourceLocation(WizzMod.MODID,"main"))
            .serverAcceptedVersions((status, version) -> true)
            .clientAcceptedVersions((status, version) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void registerPackets() {
        CHANNEL.messageBuilder(T2ListPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(T2ListPacket::save).decoder(T2ListPacket::load).consumerMainThread(T2ListPacket::handle).add();
        CHANNEL.messageBuilder(T3ListPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(T3ListPacket::save).decoder(T3ListPacket::load).consumerMainThread(T3ListPacket::handle).add();
        CHANNEL.messageBuilder(CodexScreenPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CodexScreenPacket::save).decoder(CodexScreenPacket::load).consumerMainThread(CodexScreenPacket::handle).add();
        CHANNEL.messageBuilder(MortarScreenPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(MortarScreenPacket::save).decoder(MortarScreenPacket::load).consumerMainThread(MortarScreenPacket::handle).add();
        CHANNEL.messageBuilder(MortarRecipePacket.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(MortarRecipePacket::save).decoder(MortarRecipePacket::load).consumerMainThread(MortarRecipePacket::handle).add();
    }
}
