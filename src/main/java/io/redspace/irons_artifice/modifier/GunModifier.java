package io.redspace.irons_artifice.modifier;

import io.redspace.irons_artifice.data.ShotComponentMap;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface GunModifier {
    void apply(ShotComponentMap components);

    default List<Component> getDescriptionText() {
        return List.of();
    }
}
