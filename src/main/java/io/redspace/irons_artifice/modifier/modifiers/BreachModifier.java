package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.data.ValueModifier;
import io.redspace.irons_artifice.modifier.ValueStackModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BreachModifier extends ValueStackModifier {
    public BreachModifier() {
        super(Map.of(
                ShotComponents.BLOCK_DAMAGE_MULTIPLIER, new ValueModifier(0.5, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.BENEFICIAL)
        ));
    }

    @Override
    public List<Component> getDescriptionText() {
        var text = new ArrayList<>(super.getDescriptionText());
        text.add(0, Component.translatable("irons_artifice.component_type.enables_block_damage").withStyle(ChatFormatting.AQUA));
        return text;
    }

    @Override
    public void apply(ShotComponentMap components) {
        super.apply(components);
        components.set(ShotComponents.BREAKS_BLOCKS, true);
    }
}
