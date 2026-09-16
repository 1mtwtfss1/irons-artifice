package io.redspace.irons_artifice.gun;

import io.redspace.irons_artifice.data.FireCycleCueStack;
import io.redspace.irons_artifice.data.FireMode;
import io.redspace.irons_artifice.data.HandOccupancy;
import io.redspace.irons_artifice.data.PlayableSound;
import io.redspace.irons_artifice.data.ReloadCueStack;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.item.TopLoadConfig;
import io.redspace.irons_artifice.item.animation_adjuster.AnimationAdjuster;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Definition of a specific gun and its properties
 *
 * @param baseProfileSupplier supplies innate (autoattack) shot component map
 * @param magazineCapacity    rounds the magazine holds
 * @param modifierSlots       number of modifier slots on the gun
 * @param reloadTimeTicks     ticks required to reload
 */
public record GunProfile(
        Supplier<ShotComponentMap> baseProfileSupplier,
        int magazineCapacity,
        int modifierSlots,
        int reloadTimeTicks,
        FireMode fireMode,
        @Nullable TopLoadConfig topLoadConfig,
        ArmPoseKind armPoseKind,
        ReloadCueStack reloadCues,
        @Nullable PlayableSound equipSound,
        FireCycleCueStack fireCycleCues,
        List<AnimationAdjuster> animationAdjusters,
        Map<GunState, HandOccupancy> occupancyOverrides
) {
    @Deprecated(forRemoval = true)
    public GunProfile {
        animationAdjusters = List.copyOf(animationAdjusters);
        occupancyOverrides = Map.copyOf(occupancyOverrides);
    }

    public static Builder builder(int magazineCapacity, int modifierSlots, int reloadTimeTicks, FireMode fireMode, ArmPoseKind armPoseKind, Supplier<ShotComponentMap> baseProfileSuppliers) {
        return new Builder(magazineCapacity, modifierSlots, reloadTimeTicks, fireMode, armPoseKind, baseProfileSuppliers);
    }

    public ShotComponentMap baseProfile() {
        return baseProfileSupplier.get();
    }

    public HandOccupancy defaultOccupancy() {
        return armPoseKind == ArmPoseKind.RIFLE ? HandOccupancy.BOTH : HandOccupancy.MAINHAND;
    }

    public HandOccupancy occupancyFor(GunState state) {
        return occupancyOverrides.getOrDefault(state, defaultOccupancy());
    }

    public static final class Builder {
        public static final int DEFAULT_MAGAZINE_CAPACITY = 1;
        public static final int DEFAULT_MODIFIER_SLOTS = 5;
        public static final int DEFAULT_RELOAD_TIME_TICKS = 40;


        private final Supplier<ShotComponentMap> baseProfileSuppliers;
        private final int magazineCapacity;
        private final int modifierSlots;
        private final int reloadTimeTicks;
        private final FireMode fireMode;
        private final ArmPoseKind armPoseKind;

        private @Nullable TopLoadConfig topLoadConfig = null;
        private ReloadCueStack reloadCues = ReloadCueStack.EMPTY;
        private @Nullable PlayableSound equipSound = null;
        private FireCycleCueStack fireCycleCues = FireCycleCueStack.EMPTY;
        private List<AnimationAdjuster> animationAdjusters = List.of();
        private Map<GunState, HandOccupancy> occupancyOverrides = Map.of();

        private Builder(int magazineCapacity, int modifierSlots, int reloadTimeTicks, FireMode fireMode, ArmPoseKind armPoseKind, Supplier<ShotComponentMap> baseProfileSuppliers) {
            this.baseProfileSuppliers = baseProfileSuppliers;
            this.magazineCapacity = magazineCapacity;
            this.modifierSlots = modifierSlots;
            this.reloadTimeTicks = reloadTimeTicks;
            this.fireMode = fireMode;
            this.armPoseKind = armPoseKind;
        }

        public Builder topLoadConfig(@Nullable TopLoadConfig topLoadConfig) {
            this.topLoadConfig = topLoadConfig;
            return this;
        }

        public Builder reloadCues(ReloadCueStack reloadCues) {
            this.reloadCues = reloadCues;
            return this;
        }

        public Builder equipSound(@Nullable PlayableSound equipSound) {
            this.equipSound = equipSound;
            return this;
        }

        public Builder fireCycleCues(FireCycleCueStack fireCycleCues) {
            this.fireCycleCues = fireCycleCues;
            return this;
        }

        public Builder animationAdjusters(AnimationAdjuster... animationAdjusters) {
            this.animationAdjusters = List.of(animationAdjusters);
            return this;
        }

        public Builder occupancy(Map<GunState, HandOccupancy> occupancyOverrides) {
            this.occupancyOverrides = occupancyOverrides;
            return this;
        }

        public Builder occupancy(GunState state, HandOccupancy occupancy) {
            this.occupancyOverrides = Map.of(state, occupancy);
            return this;
        }

        @SuppressWarnings({"deprecation", "removal"})
        public GunProfile build() {
            return new GunProfile(
                    baseProfileSuppliers,
                    magazineCapacity,
                    modifierSlots,
                    reloadTimeTicks,
                    fireMode,
                    topLoadConfig,
                    armPoseKind,
                    reloadCues,
                    equipSound,
                    fireCycleCues,
                    animationAdjusters,
                    occupancyOverrides
            );
        }
    }
}
