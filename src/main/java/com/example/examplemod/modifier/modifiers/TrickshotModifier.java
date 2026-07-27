package com.example.examplemod.modifier.modifiers;

import com.example.examplemod.data.PlayableSound;
import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.data.ValueModifier;
import com.example.examplemod.modifier.ValueStackModifier;
import com.example.examplemod.registry.SoundRegistry;

import java.util.Map;

public final class TrickshotModifier extends ValueStackModifier {
    public TrickshotModifier() {
        super(Map.of(
                ShotComponents.RICOCHET, new ValueModifier(1, ValueModifier.Operation.ADD, ValueModifier.Type.BENEFICIAL)
        ));
    }

    @Override
    public void apply(ShotComponentMap components) {
        super.apply(components);
        components.getOrCreate(ShotComponents.IMPACT_SOUND).addBlockAccent(PlayableSound.of(SoundRegistry.BULLET_IMPACT_RICOCHET, 2f, .7f, 1.3f));
    }
}
