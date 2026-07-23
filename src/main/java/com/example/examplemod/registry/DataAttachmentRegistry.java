package com.example.examplemod.registry;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.recoil.RecoilState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class DataAttachmentRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ExampleMod.MODID);

    public static final Supplier<AttachmentType<RecoilState>> RECOIL =
            ATTACHMENT_TYPES.register("recoil",
                    () -> AttachmentType.builder(() -> RecoilState.NONE).build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
