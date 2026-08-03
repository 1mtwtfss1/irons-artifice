package io.redspace.irons_artifice.modifier.on_hit_handlers;

import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.gun.HitEntityAccumulator;
import io.redspace.irons_artifice.gun.OnHitEffect;
import io.redspace.irons_artifice.utils.CombatHelper;
import io.redspace.irons_artifice.utils.Utils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlackpowderPayloadOnHit implements OnHitEffect {
    private static final float RADIUS = 3f;
    private static final float DAMAGE_FRACTION = 1f;

    @Override
    public void onHit(ServerLevel level, Bullet bullet, HitResult hitResult, HitEntityAccumulator accumulator) {
        Vec3 center = hitResult.getLocation();
        float radiusSq = RADIUS * RADIUS;
        AABB area = AABB.ofSize(center, RADIUS * 2, RADIUS * 2, RADIUS * 2);
        float baseDamage = bullet.resolveDamage() * DAMAGE_FRACTION;
        Entity owner = bullet.getOwner();

        for (Entity entity : level.getEntities(bullet, area, e ->
                e.canBeHitByProjectile() && CombatHelper.canHarm(owner, e))) {
            if (accumulator.contains(entity)) {
                continue;
            }
            double distSq = entity.getBoundingBox().getCenter().distanceToSqr(center);
            if (distSq > radiusSq) {
                continue;
            }
            float falloff = 1f - (float) Math.sqrt(distSq) / RADIUS;
            float damage = baseDamage * falloff;
            if (damage <= 0) {
                continue;
            }
            if (entity.hurtServer(level, bullet.damageSources().explosion(bullet, owner instanceof LivingEntity living ? living : null), damage)) {
                accumulator.add(entity);
            }
        }

        level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 2.5f, 1.4f);
        Utils.spawnParticles(level, ParticleTypes.EXPLOSION, center.x, center.y, center.z, 1, 0, 0, 0, 0, true);
        Utils.spawnParticles(level, ParticleTypes.SMOKE, center.x, center.y, center.z, 8, 0.4, 0.4, 0.4, 0.02, false);
        Utils.spawnParticles(level, ParticleTypes.LAVA, center.x, center.y, center.z, 6, 0.35, 0.35, 0.35, 0.01, false);
    }
}
