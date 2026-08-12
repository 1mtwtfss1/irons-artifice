package io.redspace.irons_artifice.api;

import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.gun.GunProfile;
import io.redspace.irons_artifice.menu.GunContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class ComposeShotEvent extends LivingEvent {
    private final GunProfile gunProfile;
    private final GunContainer modifiers;
    private final ShotComponentMap components;
    private final ItemStack gunStack;

    public ComposeShotEvent(LivingEntity entity, GunProfile gunProfile, GunContainer modifiers, ShotComponentMap components, ItemStack gunStack) {
        super(entity);
        this.gunProfile = gunProfile;
        this.modifiers = modifiers;
        this.components = components;
        this.gunStack = gunStack;
    }

    public ShotComponentMap getComponents() {
        return components;
    }

    public GunContainer getModifiers() {
        return modifiers;
    }

    public ItemStack getGunStack() {
        return gunStack;
    }

    public GunProfile getGunProfile() {
        return gunProfile;
    }
}
