package com.example.examplemod.gun;

import com.example.examplemod.data.ShotComponentMap;

import java.util.function.Supplier;

/**
 * Definition of a gun archetype, ie Revolver
 *
 * @param baseProfileSupplier supplies innate (autoattack) shot component map
 * @param magazineCapacity    rounds the magazine holds
 * @param modifierSlots       number of modifier slots on the gun
 * @param reloadTimeTicks     ticks required to reload
 * @param fireMode            how a held attack input translates into shots (semi vs full auto)
 */
public record GunProfile(
        Supplier<ShotComponentMap> baseProfileSupplier,
        int magazineCapacity,
        int modifierSlots,
        int reloadTimeTicks,
        FireMode fireMode
) {
    public ShotComponentMap baseProfile() {
        return baseProfileSupplier.get();
    }
}
