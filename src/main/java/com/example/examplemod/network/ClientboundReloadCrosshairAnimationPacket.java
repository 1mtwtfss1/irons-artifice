package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.ClientHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundReloadCrosshairAnimationPacket(int reloadDuration)
        implements CustomPacketPayload {

    public static final Type<ClientboundReloadCrosshairAnimationPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ExampleMod.MODID, "reload_crosshair_animation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundReloadCrosshairAnimationPacket> STREAM_CODEC =
            StreamCodec.of(ClientboundReloadCrosshairAnimationPacket::encode, ClientboundReloadCrosshairAnimationPacket::decode);

    private static void encode(RegistryFriendlyByteBuf buf, ClientboundReloadCrosshairAnimationPacket msg) {
        buf.writeVarInt(msg.reloadDuration);
    }

    private static ClientboundReloadCrosshairAnimationPacket decode(RegistryFriendlyByteBuf buf) {
        return new ClientboundReloadCrosshairAnimationPacket(buf.readVarInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundReloadCrosshairAnimationPacket payload, IPayloadContext context) {
        ClientHelper.handleCrosshairAnimation(payload);
    }
}
