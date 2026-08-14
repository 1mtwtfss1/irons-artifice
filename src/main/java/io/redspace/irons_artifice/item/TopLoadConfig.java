package io.redspace.irons_artifice.item;

public record TopLoadConfig(double loopStart, double loopEnd, double loopDuration) {
    public double resumeFrom(int topLoadCount) {
        return loopEnd - loopDuration * (topLoadCount - 1);
    }
}
