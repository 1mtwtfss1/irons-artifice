package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.ClientHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record ClientboundBulletTrailPacket(Vec3 from, Vec3 to, List<ParticleOptions> particles)
        implements CustomPacketPayload {

    public static final Type<ClientboundBulletTrailPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ExampleMod.MODID, "bullet_trail"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBulletTrailPacket> STREAM_CODEC =
            StreamCodec.of(ClientboundBulletTrailPacket::encode, ClientboundBulletTrailPacket::decode);

    private static void encode(RegistryFriendlyByteBuf buf, ClientboundBulletTrailPacket msg) {
        buf.writeDouble(msg.from.x);
        buf.writeDouble(msg.from.y);
        buf.writeDouble(msg.from.z);
        buf.writeDouble(msg.to.x);
        buf.writeDouble(msg.to.y);
        buf.writeDouble(msg.to.z);
        buf.writeVarInt(msg.particles.size());
        for (ParticleOptions particle : msg.particles) {
            ParticleTypes.STREAM_CODEC.encode(buf, particle);
        }
    }

    private static ClientboundBulletTrailPacket decode(RegistryFriendlyByteBuf buf) {
        Vec3 from = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 to = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        int size = buf.readVarInt();
        List<ParticleOptions> particles = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            particles.add(ParticleTypes.STREAM_CODEC.decode(buf));
        }
        return new ClientboundBulletTrailPacket(from, to, particles);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundBulletTrailPacket payload, IPayloadContext context) {
        ClientHelper.handleBulletTrail(payload);
    }
}
