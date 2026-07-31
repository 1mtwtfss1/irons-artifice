package com.example.examplemod.registry;

import com.example.examplemod.ExampleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class SoundRegistry {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, ExampleMod.MODID);

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    public static DeferredHolder<SoundEvent, SoundEvent> BULLET_IMPACT_GENERIC = registerSoundEvent("entity.bullet.impact.generic");
    public static DeferredHolder<SoundEvent, SoundEvent> BULLET_IMPACT_RICOCHET = registerSoundEvent("entity.bullet.impact.ricochet");
    public static DeferredHolder<SoundEvent, SoundEvent> GENERIC_BULLET_ECHO = registerSoundEvent("entity.bullet.echo.generic");
    public static DeferredHolder<SoundEvent, SoundEvent> REVOLVER_SHOOT = registerSoundEvent("item.example_revolver.shoot");
    public static DeferredHolder<SoundEvent, SoundEvent> COCK_HAMMER = registerSoundEvent("item.generic.cock_hammer");
    public static DeferredHolder<SoundEvent, SoundEvent> BLACKPOWDER_REVOLVER_RELOAD_START = registerSoundEvent("item.blackpowder_revolver.reload.start");
    public static DeferredHolder<SoundEvent, SoundEvent> BLACKPOWDER_REVOLVER_RELOAD_MID = registerSoundEvent("item.blackpowder_revolver.reload.mid");
    public static DeferredHolder<SoundEvent, SoundEvent> BLACKPOWDER_REVOLVER_RELOAD_END = registerSoundEvent("item.blackpowder_revolver.reload.end");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ExampleMod.id(name)));
    }
}
