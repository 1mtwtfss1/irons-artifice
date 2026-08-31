package io.redspace.irons_artifice.advancement;

import java.util.UUID;

/**
 * Server-only identity for one fired projectile and the trigger-pull it came from.
 */
public final class ShotRecord {
    private final UUID fireId;
    private final UUID lineageId;
    private final boolean root;
    private final boolean fullMagazine;
    private boolean ricocheted;

    private ShotRecord(UUID fireId, UUID lineageId, boolean root, boolean fullMagazine, boolean ricocheted) {
        this.fireId = fireId;
        this.lineageId = lineageId;
        this.root = root;
        this.fullMagazine = fullMagazine;
        this.ricocheted = ricocheted;
    }

    public static ShotRecord rootPellet(UUID fireId, boolean fullMagazine) {
        return new ShotRecord(fireId, UUID.randomUUID(), true, fullMagazine, false);
    }

    public ShotRecord child(boolean bounceBorn) {
        return new ShotRecord(fireId, lineageId, false, fullMagazine, ricocheted || bounceBorn);
    }

    public UUID fireId() {
        return fireId;
    }

    public UUID lineageId() {
        return lineageId;
    }

    public boolean root() {
        return root;
    }

    public boolean fullMagazine() {
        return fullMagazine;
    }

    public boolean ricocheted() {
        return ricocheted;
    }

    public void markRicocheted() {
        this.ricocheted = true;
    }
}
