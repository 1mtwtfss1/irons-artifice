package io.redspace.irons_artifice.gun;

import io.redspace.irons_artifice.data.ComponentType;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.Value;
import io.redspace.irons_artifice.item.MagazineContents;
import net.minecraft.world.item.ItemStack;

public record ShotProfile(ItemStack itemStack, GunProfile gun, MagazineContents magazineContents,
                          ShotComponentMap components) {

    public <T> T get(ComponentType<T> type) {
        return components.getOrDefault(type);
    }

    public <T> void remove(ComponentType<T> type) {
        components.remove(type);
    }

    public double value(ComponentType<Value> type) {
        return components.getOrDefault(type).compute();
    }
}
