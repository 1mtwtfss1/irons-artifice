package io.redspace.irons_artifice.gun;

import java.util.ArrayList;
import java.util.List;

public final class PostHitEffects {
    private final List<PostHitEffect> effects = new ArrayList<>();

    public void add(PostHitEffect effect) {
        this.effects.add(effect);
    }

    public boolean contains(PostHitEffect effect) {
        return this.effects.contains(effect);
    }

    public List<PostHitEffect> all() {
        return List.copyOf(this.effects);
    }

    public boolean isEmpty() {
        return this.effects.isEmpty();
    }
}
