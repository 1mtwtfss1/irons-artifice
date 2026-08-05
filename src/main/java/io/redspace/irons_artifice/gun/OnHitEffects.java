package io.redspace.irons_artifice.gun;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class OnHitEffects {
    private final List<OnHitEffect> effects = new ArrayList<>();

    public void add(OnHitEffect effect) {
        this.effects.add(effect);
    }

    /**
     * Returns the first effect of {@code type}, or creates/stores one via {@code factory} if absent.
     */
    @SuppressWarnings("unchecked")
    public <T extends OnHitEffect> T getOrCreate(Class<T> type, Supplier<T> factory) {
        for (OnHitEffect effect : effects) {
            if (type.isInstance(effect)) {
                return (T) effect;
            }
        }
        T created = factory.get();
        effects.add(created);
        return created;
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
