package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.ClientHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundGunshotEchoPacket(Holder<SoundEvent> sound, SoundSource source,
                                            double x, double y, double z,
                                            float minPitch, float maxPitch)
        implements CustomPacketPayload {

    public static final Type<ClientboundGunshotEchoPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ExampleMod.MODID, "gunshot_echo"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundGunshotEchoPacket> STREAM_CODEC =
            StreamCodec.of(ClientboundGunshotEchoPacket::encode, ClientboundGunshotEchoPacket::decode);

    private static void encode(RegistryFriendlyByteBuf buf, ClientboundGunshotEchoPacket msg) {
        SoundEvent.STREAM_CODEC.encode(buf, msg.sound);
        buf.writeEnum(msg.source);
        buf.writeInt((int) (msg.x));
        buf.writeInt((int) (msg.y));
        buf.writeInt((int) (msg.z));
        buf.writeFloat(msg.minPitch);
        buf.writeFloat(msg.maxPitch);
    }

    private static ClientboundGunshotEchoPacket decode(RegistryFriendlyByteBuf buf) {
        Holder<SoundEvent> sound = SoundEvent.STREAM_CODEC.decode(buf);
        SoundSource source = buf.readEnum(SoundSource.class);
        double x = buf.readInt();
        double y = buf.readInt();
        double z = buf.readInt();
        float minPitch = buf.readFloat();
        float maxPitch = buf.readFloat();
        return new ClientboundGunshotEchoPacket(sound, source, x, y, z, minPitch, maxPitch);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundGunshotEchoPacket payload, IPayloadContext context) {
        ClientHelper.handleGunshotEcho(payload);
    }
}
