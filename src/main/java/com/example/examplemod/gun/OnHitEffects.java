package com.example.examplemod.gun;

import java.util.ArrayList;
import java.util.List;

public final class OnHitEffects {
    private final List<OnHitEffect> effects = new ArrayList<>();

    public void add(OnHitEffect effect) {
        this.effects.add(effect);
    }

    public boolean contains(OnHitEffect effect) {
        return this.effects.contains(effect);
    }

    public List<OnHitEffect> all() {
        return List.copyOf(this.effects);
    }

    public boolean isEmpty() {
        return this.effects.isEmpty();
    }
}
