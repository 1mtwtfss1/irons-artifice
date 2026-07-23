package com.example.examplemod.gun;

import com.example.examplemod.data.ShotComponentMap;

import java.util.function.Supplier;

/**
 * Definition of a gun archetype, ie Revolver
 *
 * @param baseProfileSupplier supplies innate (autoattack) shot component map
 * @param magazineCapacity    rounds the magazine holds
 * @param modifierSlots       number of modifier slots on the gun
 * @param fireCooldownTicks   fire rate ish
 */
public record GunProfile(
        Supplier<ShotComponentMap> baseProfileSupplier,
        int magazineCapacity,
        int modifierSlots,
        int fireCooldownTicks
) {
    public ShotComponentMap baseProfile() {
        return baseProfileSupplier.get();
    }
}
