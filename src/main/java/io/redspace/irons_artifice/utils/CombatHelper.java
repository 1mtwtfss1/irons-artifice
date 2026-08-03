package io.redspace.irons_artifice.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public final class CombatHelper {

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
        if (target instanceof Player player && attacker instanceof Player player1 && !player1.canHarmPlayer(player)) {
            return false;
        }
        return true;
    }
}
