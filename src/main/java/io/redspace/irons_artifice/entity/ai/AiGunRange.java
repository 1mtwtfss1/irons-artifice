package io.redspace.irons_artifice.entity.ai;

public record AiGunRange(float maxRange, float panicRange, float idealRangeMin, float idealRangeMax) {
    public AiGunRange(float maxRange) {
        this(maxRange, maxRange * 0.125f, maxRange * 0.35f, maxRange * 0.75f);
    }

    public float panicSqr() {
        return panicRange * panicRange;
    }

    public float idealMinSqr() {
        return idealRangeMin * idealRangeMin;
    }

    public float idealMaxSqr() {
        return idealRangeMax * idealRangeMax;
    }

    public float maxRangeSqr() {
        return maxRange * maxRange;
    }
}
