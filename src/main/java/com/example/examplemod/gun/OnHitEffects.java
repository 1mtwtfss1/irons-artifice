package com.example.examplemod.gun;

import java.util.EnumSet;
import java.util.Set;

public final class OnHitEffects {
    private final Set<OnHitEffect> effects = EnumSet.noneOf(OnHitEffect.class);

    public void add(OnHitEffect effect) {
        this.effects.add(effect);
    }

    public boolean contains(OnHitEffect effect) {
        return this.effects.contains(effect);
    }

    public Set<OnHitEffect> all() {
        return Set.copyOf(this.effects);
    }

    public boolean isEmpty() {
        return this.effects.isEmpty();
    }
}
