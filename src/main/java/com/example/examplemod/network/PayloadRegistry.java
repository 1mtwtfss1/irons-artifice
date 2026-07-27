package com.example.examplemod.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class PayloadRegistry {
    private static final String VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);

        registrar.playToServer(
                ServerboundFireGunPacket.TYPE,
                ServerboundFireGunPacket.STREAM_CODEC,
                ServerboundFireGunPacket::handle
        );
        registrar.playToServer(
                ServerboundOpenModifierMenuPacket.TYPE,
                ServerboundOpenModifierMenuPacket.STREAM_CODEC,
                ServerboundOpenModifierMenuPacket::handle
        );

        registrar.playToClient(
                ClientboundBulletTrailPacket.TYPE,
                ClientboundBulletTrailPacket.STREAM_CODEC,
                ClientboundBulletTrailPacket::handle
        );
        registrar.playToClient(
                ClientboundReloadCrosshairAnimationPacket.TYPE,
                ClientboundReloadCrosshairAnimationPacket.STREAM_CODEC,
                ClientboundReloadCrosshairAnimationPacket::handle
        );
        registrar.playToClient(
                ClientboundBulletImpactPacket.TYPE,
                ClientboundBulletImpactPacket.STREAM_CODEC,
                ClientboundBulletImpactPacket::handle
        );
        registrar.playToClient(
                ClientboundGunAnimationPacket.TYPE,
                ClientboundGunAnimationPacket.STREAM_CODEC,
                ClientboundGunAnimationPacket::handle
        );
        registrar.playToClient(
                ClientboundGunshotEchoPacket.TYPE,
                ClientboundGunshotEchoPacket.STREAM_CODEC,
                ClientboundGunshotEchoPacket::handle
        );
    }
}
