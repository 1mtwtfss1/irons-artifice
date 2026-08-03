package io.redspace.irons_artifice.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

/**
 * Shared combat filters for on-hit effects. Other handlers with friendly-fire TODOs can migrate here later.
 */
public final class CombatHelper {
    private CombatHelper() {
    }

    public static boolean canHarm(@Nullable Entity attacker, @Nullable Entity target) {
        if (attacker == null || target == null || attacker == target) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        if (target.isAlliedTo(attacker) || attacker.isAlliedTo(target)) {
            return false;
        }
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }
        return true;
    }
}
