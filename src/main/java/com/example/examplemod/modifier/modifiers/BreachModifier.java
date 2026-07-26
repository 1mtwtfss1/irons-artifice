package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.ValueStackModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BreachModifier extends ValueStackModifier {
    public BreachModifier() {
        super(Map.of(
                ShotComponents.BLOCK_DAMAGE_MULTIPLIER, new ValueModifier(0.25, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.BENEFICIAL)
        ));
    }

    @Override
    public List<Component> getDescriptionText() {
        var text = new ArrayList<>(super.getDescriptionText());
        text.add(0, Component.translatable("examplemod.component_type.enables_block_damage").withStyle(ChatFormatting.GREEN));
        return text;
    }

    @Override
    public void apply(ShotComponentMap components) {
        super.apply(components);
        components.set(ShotComponents.BREAKS_BLOCKS, true);
    }
}
