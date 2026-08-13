package io.redspace.irons_artifice.api;

import io.redspace.irons_artifice.gun.ShotProfile;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class ComposeShotEvent extends LivingEvent {
    private final ShotProfile shotProfile;

    public ComposeShotEvent(LivingEntity entity, ShotProfile shotProfile) {
        super(entity);

        this.shotProfile = shotProfile;
    }

    public ShotProfile getShotProfile() {
        return shotProfile;
    }
}
