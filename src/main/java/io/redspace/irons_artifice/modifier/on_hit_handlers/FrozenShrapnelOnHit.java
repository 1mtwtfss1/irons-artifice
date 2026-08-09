package io.redspace.irons_artifice.modifier.on_hit_handlers;

import io.redspace.irons_artifice.client.particle.ColorTransitionParticleOption;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.data.Value;
import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.gun.HitEntityAccumulator;
import io.redspace.irons_artifice.gun.OnHitEffect;
import io.redspace.irons_artifice.gun.ShotProfile;
import io.redspace.irons_artifice.item.MagazineContents;
import io.redspace.irons_artifice.registry.EntityRegistry;
import io.redspace.irons_artifice.registry.ParticleRegistry;
import io.redspace.irons_artifice.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FrozenShrapnelOnHit implements OnHitEffect {
    public static final int SHRAPNEL_COUNT = 5;
    public static final float DAMAGE_FRACTION = 0.5f;
    public static final float SPEED = 3f;
    public static final float DRAG = 0.8f;
    public static final float CONE_HALF_ANGLE_DEG = 30f;
    public static final float SPAWN_OFFSET = 0.1f;
    public static final int TRAIL_COLOR_FROM = 0xE8F7FF;
    public static final int TRAIL_COLOR_TO = 0x7EC8FF;

    @Override
    public void onHit(ServerLevel level, Bullet bullet, HitResult hitResult, HitEntityAccumulator accumulator) {
        ShotProfile parentProfile = bullet.getProfile();
        if (parentProfile == null) {
            return;
        }

        Vec3 incoming = bullet.getDeltaMovement();
        if (incoming.lengthSqr() < 1.0E-8) {
            return;
        }
        Vec3 axis = incoming.normalize();
        if (hitResult instanceof BlockHitResult blockHit) {
            axis = Utils.reflect(incoming, blockHit.getDirection().getUnitVec3()).normalize();
            if (axis.lengthSqr() < 1.0E-8) {
                return;
            }
        }

        Vec3 origin = hitResult.getLocation().add(axis.scale(SPAWN_OFFSET));
        Entity hitEntity = hitResult instanceof EntityHitResult entityHit ? entityHit.getEntity() : null;
        float childDamage = bullet.resolveDamage() * DAMAGE_FRACTION;
        RandomSource random = level.getRandom();
        float halfAngleRad = CONE_HALF_ANGLE_DEG * Mth.DEG_TO_RAD;

        for (int i = 0; i < SHRAPNEL_COUNT; i++) {
            Vec3 direction = randomDirectionInCone(axis, halfAngleRad, random);
            ShotProfile childProfile = createChildProfile(parentProfile, childDamage);
            Bullet child = new Bullet(EntityRegistry.BULLET.get(), level);
            child.setOwner(bullet.getOwner());
            child.applyProfile(childProfile);
            child.setPos(origin);
            if (hitEntity != null) {
                child.markPierced(hitEntity);
            }
            child.shoot(direction.x, direction.y, direction.z, SPEED, 0f);
            level.addFreshEntity(child);
        }
    }

    private static ShotProfile createChildProfile(ShotProfile parent, float damage) {
        ShotComponentMap components = new ShotComponentMap();
        components.set(ShotComponents.DAMAGE, Value.of(damage));
        components.set(ShotComponents.PROJECTILE_COUNT, Value.of(1));
        components.set(ShotComponents.BULLET_SPEED, Value.of(SPEED));
        components.set(ShotComponents.BULLET_DRAG, Value.of(DRAG));
        components.getOrCreate(ShotComponents.PARTICLE_TRAIL).add(new ColorTransitionParticleOption(
                ParticleRegistry.BULLET_TRAIL.get(), TRAIL_COLOR_FROM, TRAIL_COLOR_TO, 1f, 0f, 1f, 1f, 0.45f, 0f, 0
        ));
        components.getOrCreate(ShotComponents.POST_HIT_EFFECTS).add(new FreezePostHit());

        ItemStack stack = parent.itemStack() != null ? parent.itemStack() : ItemStack.EMPTY;
        MagazineContents magazine = parent.magazineContents() != null ? parent.magazineContents() : MagazineContents.EMPTY;
        return new ShotProfile(stack, parent.gun(), magazine, components);
    }

    /**
     * Uniform-ish random unit direction within a cone of the given half-angle around {@code axis}.
     */
    static Vec3 randomDirectionInCone(Vec3 axis, float halfAngleRad, RandomSource random) {
        Vec3 n = axis.normalize();
        // Orthonormal basis perpendicular to axis
        Vec3 arbitrary = Math.abs(n.y) < 0.99 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 u = n.cross(arbitrary).normalize();
        Vec3 v = n.cross(u);

        float cosHalf = Mth.cos(halfAngleRad);
        float z = Mth.lerp(random.nextFloat(), cosHalf, 1f);
        float r = Mth.sqrt(1f - z * z);
        float phi = random.nextFloat() * Mth.TWO_PI;
        float cosPhi = Mth.cos(phi);
        float sinPhi = Mth.sin(phi);

        return n.scale(z).add(u.scale(r * cosPhi)).add(v.scale(r * sinPhi)).normalize();
    }
}
