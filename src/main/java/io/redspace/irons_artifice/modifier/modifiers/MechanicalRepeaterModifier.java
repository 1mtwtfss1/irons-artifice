package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.modifier.GunModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class MechanicalRepeaterModifier implements GunModifier {
    @Override
    public void apply(ShotComponentMap components) {
        components.set(ShotComponents.FORCE_AUTO_FIRE, true);
    }

    @Override
    public List<Component> getDescriptionText() {
        return List.of(Component.translatable("irons_artifice.component_type.force_auto_fire").withStyle(ChatFormatting.AQUA));
    }
}
