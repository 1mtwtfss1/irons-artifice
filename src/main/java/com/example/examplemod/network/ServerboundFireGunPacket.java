package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.GunplayManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundFireGunPacket(Vec3 direction)
        implements CustomPacketPayload {

    public static final Type<ServerboundFireGunPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ExampleMod.MODID, "fire_gun"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundFireGunPacket> STREAM_CODEC =
            StreamCodec.of(ServerboundFireGunPacket::encode, ServerboundFireGunPacket::decode);

    private static void encode(RegistryFriendlyByteBuf buf, ServerboundFireGunPacket msg) {
        buf.writeDouble(msg.direction.x);
        buf.writeDouble(msg.direction.y);
        buf.writeDouble(msg.direction.z);
    }

    private static ServerboundFireGunPacket decode(RegistryFriendlyByteBuf buf) {
        return new ServerboundFireGunPacket(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundFireGunPacket payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            GunplayManager.tryFire(serverPlayer, payload.direction());
        }
    }
}
