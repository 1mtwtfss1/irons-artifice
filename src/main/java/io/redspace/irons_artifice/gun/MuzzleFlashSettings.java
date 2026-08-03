package io.redspace.irons_artifice.gun;

import net.minecraft.util.RandomSource;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public record MuzzleFlashSettings(
        Set<MuzzleFlashType> types,
        float muzzleDistanceScalar,
        float tintR,
        float tintG,
        float tintB
) {
    public static final MuzzleFlashSettings DEFAULT = of(1.5f, MuzzleFlashType.TRIANGLE, MuzzleFlashType.SMALL_STAR);

    public MuzzleFlashSettings {
        types = Set.copyOf(types);
    }

    public static MuzzleFlashSettings of(float muzzleDistanceScalar, MuzzleFlashType... types) {
        if (types.length == 0) {
            return new MuzzleFlashSettings(Set.of(), muzzleDistanceScalar, 1f, 1f, 1f);
        }
        return new MuzzleFlashSettings(EnumSet.copyOf(List.of(types)), muzzleDistanceScalar, 1f, 1f, 1f);
    }

    public MuzzleFlashSettings withTint(float r, float g, float b) {
        return new MuzzleFlashSettings(types, muzzleDistanceScalar, r, g, b);
    }

    public boolean isTinted() {
        return tintR != 1f || tintG != 1f || tintB != 1f;
    }

    public MuzzleFlashType pick(RandomSource random) {
        if (types.isEmpty()) {
            throw new IllegalStateException("MuzzleFlashSettings has no types to pick from");
        }
        return types.stream().skip(random.nextInt(types.size())).findFirst().orElseThrow();
    }
}
