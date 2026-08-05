package io.redspace.irons_artifice.modifier.on_hit_handlers;

import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.gun.HitEntityAccumulator;
import io.redspace.irons_artifice.gun.OnHitEffect;
import io.redspace.irons_artifice.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GravityWellOnHit implements OnHitEffect {
    @Override
    public void onHit(ServerLevel level, Bullet bullet, HitResult hitResult, HitEntityAccumulator accumulator) {
        Vec3 center = hitResult.getLocation();
        AABB area = AABB.ofSize(center, 4, 4, 4);
        Entity owner = bullet.getOwner();
        bullet.level().getEntities(bullet, area, entity ->
                entity.canBeHitByProjectile() && Utils.canHarm(owner, entity)
        ).forEach(
                entity -> {
                    if (entity.getBoundingBox().getCenter().distanceToSqr(center) < 3 * 3) {
                        entity.setDeltaMovement(entity.getDeltaMovement().add(center.subtract(entity.position())).scale(0.5));
                        entity.hurtMarked = true;
                    }
                }
        );
        // todo: vfx/sound
    }
}
