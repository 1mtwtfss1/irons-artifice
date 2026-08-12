package io.redspace.irons_artifice.events;

import io.redspace.irons_artifice.entity.Gunslinger;
import io.redspace.irons_artifice.registry.EntityRegistry;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public final class CommonSetup {
    public static void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.GUNSLINGER.get(), Gunslinger.createAttributes().build());
    }
}
