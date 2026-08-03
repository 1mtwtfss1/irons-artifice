package io.redspace.irons_artifice.modifier.on_hit_handlers;

import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.gun.HitEntityAccumulator;
import io.redspace.irons_artifice.gun.OnHitEffect;
import io.redspace.irons_artifice.utils.Utils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ChainLightningOnHit implements OnHitEffect {
    @Override
    public void onHit(ServerLevel level, Bullet bullet, HitResult hitResult, HitEntityAccumulator accumulator) {
        Vec3 center = hitResult.getLocation();
        float range = 6;
        AABB area = AABB.ofSize(center, range, range, range).inflate(1);
        // todo: friendlyfire or something
        // todo: vfx/sound
        // todo: multiple chains per modifier?
        var random = bullet.getRandom();
        // todo: line of sight check
        var targets = bullet.level().getEntities(bullet, area, entity ->
                !accumulator.contains(entity)
                        && entity.canBeHitByProjectile()
                        && entity.getBoundingBox().getCenter().distanceToSqr(center) < range * range);
        Vec3 visualAnchor = center.add(new Vec3(random.nextFloat(), random.nextFloat(), random.nextFloat()).scale(range).subtract(new Vec3(range, range, range).scale(0.5)));
        if (!targets.isEmpty()) {
            Entity entity = targets.get(random.nextInt(targets.size()));
            // todo: damage number?
            float damage = bullet.resolveDamage() * 0.25f;
            if (entity.hurtServer(level, bullet.damageSources().indirectMagic(bullet, bullet.getOwner()), damage)) {
                accumulator.add(entity);
            }
            visualAnchor = entity.getBoundingBox().getCenter();
        }
        float particles = range * 2;
        for (int i = 0; i < particles; i++) {
            Vec3 pos = center.lerp(visualAnchor, i / particles);
            Utils.spawnParticles(level, ParticleTypes.SCULK_CHARGE_POP, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0, false);
        }
    }
}
