package io.redspace.irons_artifice.gun;

import net.minecraft.util.RandomSource;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public record MuzzleFlashSettings(
        Set<MuzzleFlashType> types,
        float muzzleDistanceScalar,
        List<Vector3f> tints
) {
    public static final Vector3f WHITE = new Vector3f(1f, 1f, 1f);
    public static final MuzzleFlashSettings DEFAULT = of(1.5f, MuzzleFlashType.TRIANGLE, MuzzleFlashType.SMALL_STAR);

    public static MuzzleFlashSettings of(float muzzleDistanceScalar, MuzzleFlashType... types) {
        if (types.length == 0) {
            return new MuzzleFlashSettings(Set.of(), muzzleDistanceScalar, List.of());
        }
        return new MuzzleFlashSettings(EnumSet.copyOf(List.of(types)), muzzleDistanceScalar, List.of());
    }

//    /** Replaces the tint list with a single tint. */
//    public MuzzleFlashSettings withTint(Vector3f tint) {
//        return new MuzzleFlashSettings(types, muzzleDistanceScalar, List.of(new Vector3f(tint)));
//    }

    /**
     * Appends a tint to the list.
     */
    public MuzzleFlashSettings addTint(Vector3f tint) {
        List<Vector3f> next = new ArrayList<>(tints.size() + 1);
        next.addAll(tints);
        next.add(new Vector3f(tint));
        return new MuzzleFlashSettings(types, muzzleDistanceScalar, next);
    }

    public Vector3f pickTint(RandomSource random) {
        if (tints.isEmpty()) {
            return WHITE;
        }
        return tints.get(random.nextInt(tints.size()));
    }

    public MuzzleFlashType pick(RandomSource random) {
        if (types.isEmpty()) {
            throw new IllegalStateException("MuzzleFlashSettings has no types to pick from");
        }
        return types.stream().skip(random.nextInt(types.size())).findFirst().orElseThrow();
    }
}
