package com.example.examplemod.gun;

import com.example.examplemod.entity.Bullet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public interface OnHitEffect {
    void onHit(Bullet bullet, HitResult hitResult);

    OnHitEffect IGNITE = (bullet, hitresult) -> {
        if (hitresult instanceof EntityHitResult entityHitResult) {
            entityHitResult.getEntity().igniteForSeconds(3);
        }
    };

    OnHitEffect GRAVITY_WELL = (bullet, hitresult) -> {
        Vec3 center = hitresult.getLocation();
        AABB area = AABB.ofSize(center, 4, 4, 4);
        // todo: friendlyfire or something
        bullet.level().getEntities(bullet, area, Entity::canBeHitByProjectile).forEach(
                entity -> {
                    if (entity.getBoundingBox().getCenter().distanceToSqr(center) < 3 * 3) {
                        entity.setDeltaMovement(entity.getDeltaMovement().add(center.subtract(entity.position())).scale(0.5));
                        entity.hurtMarked = true;
                    }
                }
        );
        // todo: vfx/sound
    };
}
