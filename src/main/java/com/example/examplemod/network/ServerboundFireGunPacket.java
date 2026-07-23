package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.GunplayManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundFireGunPacket() implements CustomPacketPayload {
    public static final ServerboundFireGunPacket INSTANCE = new ServerboundFireGunPacket();

    public static final Type<ServerboundFireGunPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ExampleMod.MODID, "fire_gun"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundFireGunPacket> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundFireGunPacket payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            GunplayManager.tryFire(serverPlayer);
        }
    }
}
