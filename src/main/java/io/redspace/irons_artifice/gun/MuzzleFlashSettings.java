package io.redspace.irons_artifice.gun;

import net.minecraft.util.RandomSource;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public record MuzzleFlashSettings(Set<MuzzleFlashType> types, float muzzleDistanceScalar) {
    public static final MuzzleFlashSettings DEFAULT = of(1.5f, MuzzleFlashType.TRIANGLE);

    public MuzzleFlashSettings {
        types = Set.copyOf(types);
    }

    public static MuzzleFlashSettings of(float muzzleDistanceScalar, MuzzleFlashType... types) {
        if (types.length == 0) {
            return new MuzzleFlashSettings(Set.of(), muzzleDistanceScalar);
        }
        return new MuzzleFlashSettings(EnumSet.copyOf(List.of(types)), muzzleDistanceScalar);
    }

    public MuzzleFlashType pick(RandomSource random) {
        if (types.isEmpty()) {
            throw new IllegalStateException("MuzzleFlashSettings has no types to pick from");
        }
        return types.stream().skip(random.nextInt(types.size())).findFirst().orElseThrow();
    }
}
