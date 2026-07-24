package com.example.examplemod.entity;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.gun.BlockDamageManager;
import com.example.examplemod.gun.OnHitEffect;
import com.example.examplemod.gun.OnHitEffects;
import com.example.examplemod.gun.ShotProfile;
import com.example.examplemod.network.ClientboundBulletImpactPacket;
import com.example.examplemod.network.ClientboundBulletTrailPacket;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bullet extends Projectile {
    private static final EntityDataAccessor<Float> DATA_GRAVITY =
            SynchedEntityData.defineId(Bullet.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_RICOCHET =
            SynchedEntityData.defineId(Bullet.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_DRAG =
            SynchedEntityData.defineId(Bullet.class, EntityDataSerializers.FLOAT);

    private static final double BOUNCE_RETENTION = 0.8;
    private static final double TRAIL_DENSITY = 1.0;

    @Nullable
    private ShotProfile profile;
    private int piercingRemaining = 0;
    private final Set<Integer> piercedEntities = new HashSet<>();

    public Bullet(EntityType<? extends Bullet> type, Level level) {
        super(type, level);
    }

    public void applyProfile(ShotProfile profile) {
        this.profile = profile;
        this.piercingRemaining = (int) profile.value(ShotComponents.PIERCING);
        // synced parameters for movement parity
        this.entityData.set(DATA_GRAVITY, (float) profile.value(ShotComponents.GRAVITY));
        this.entityData.set(DATA_RICOCHET, (int) profile.value(ShotComponents.RICOCHET));
        this.entityData.set(DATA_DRAG, (float) profile.value(ShotComponents.BULLET_DRAG));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_GRAVITY, 0.05f);
        builder.define(DATA_RICOCHET, 0);
        builder.define(DATA_DRAG, 0.98f);
    }

    @Override
    public void tick() {
        super.tick();
        this.piercedEntities.clear();

        Vec3 delta = getDeltaMovement();
        Vec3 position = position();
        Vec3 destination = position.add(delta);

        HitResult blockHit = level().clip(new ClipContext(
                position, destination, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        if (blockHit.getType() != HitResult.Type.MISS) {
            destination = blockHit.getLocation();
        }
        Vec3 particleEnd = destination;

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                level(), this, position, destination,
                getBoundingBox().expandTowards(delta).inflate(1),
                this::canHitEntity, 0.25f);

        if (entityHit != null) {
            onHit(entityHit);
            if (this.isRemoved()) {
                // should automatically handle piercing or other effects
                particleEnd = entityHit.getLocation();
            }
        } else if (blockHit.getType() != HitResult.Type.MISS) {
            onHit(blockHit);
            particleEnd = blockHit.getLocation();
        }

        if (level() instanceof ServerLevel serverLevel && profile != null) {
            Vec3 particleStart = this.tickCount == 1 ? position.subtract(0, 0.25, 0) : position;
            emitTrail(serverLevel, particleStart, particleEnd);
        }

        this.setDeltaMovement(getDeltaMovement().scale(getDrag()));
        this.applyGravity();
        this.setPos(destination);
        if (this.getDeltaMovement().lengthSqr() < 1) {
            discard();
        }
    }

    private double irwinHall(RandomSource randomSource) {
        return (randomSource.nextDouble() + randomSource.nextDouble() + randomSource.nextDouble() + randomSource.nextDouble() - 2) / 2.0;
    }

    @Override
    public Vec3 getMovementToShoot(double xd, double yd, double zd, float pow, float uncertainty) {
        return new Vec3(xd, yd, zd)
                .normalize()
                .add(new Vec3(irwinHall(random), irwinHall(random), irwinHall(random)).normalize()
                        .scale(uncertainty * Mth.DEG_TO_RAD * 0.5))
                .scale(pow);
    }

    @Override
    protected void onHit(@NonNull HitResult hitResult) {
        super.onHit(hitResult);
        if (profile == null) {
            return;
        }
        OnHitEffects onHitEffects = profile.get(ShotComponents.ON_HIT);
        for (OnHitEffect effect : onHitEffects.all()) {
            effect.onHit(this, hitResult);
        }
    }

    @Override
    protected void onHitEntity(@NonNull EntityHitResult result) {
        super.onHitEntity(result);
        if (!(level() instanceof ServerLevel serverLevel) || profile == null) {
            return;
        }

        Entity target = result.getEntity();
        if (!piercedEntities.add(target.getId())) {
            return;
        }

        Entity owner = getOwner();
        float damage = (float) profile.value(ShotComponents.DAMAGE) / (int) profile.value(ShotComponents.PROJECTILE_COUNT);
        DamageSource source = damageSources().mobProjectile(this, owner instanceof LivingEntity le ? le : null);
        target.hurtServer(serverLevel, source, damage);

        float knockback = (float) profile.value(ShotComponents.KNOCKBACK);
        if (target instanceof LivingEntity living && knockback > 0.0F) {
            Vec3 v = getDeltaMovement();
            living.knockback(knockback, -v.x, -v.z);
        }

        if (piercingRemaining > 0) {
            piercingRemaining--;
        } else {
            discard();
        }
    }

    @Override
    protected void onHitBlock(@NonNull BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        int ricochet = this.entityData.get(DATA_RICOCHET);
        level().playSound(null, hitResult.getBlockPos(), level().getBlockState(hitResult.getBlockPos()).getSoundType(level(), hitResult.getBlockPos(), null).getBreakSound(), SoundSource.BLOCKS, .75f, 1f);
        if (level() instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, this.chunkPosition(), new ClientboundBulletImpactPacket(hitResult.getLocation(), this.getDeltaMovement(), hitResult.getDirection().getUnitVec3()));
            if (this.profile != null) {
                // todo: block damage mujltipliers
                // fixme: why is damage not resolved already
                BlockDamageManager.applyDamage(serverLevel, hitResult.getBlockPos(), level().getBlockState(hitResult.getBlockPos()), (float) this.profile.value(ShotComponents.DAMAGE) / (float) profile.value(ShotComponents.PROJECTILE_COUNT), this);
            }
        }
        if (ricochet > 0) {
            this.entityData.set(DATA_RICOCHET, ricochet - 1);
            reflect(hitResult.getDirection());
            return;
        }
        discard();
    }

    private void reflect(Direction face) {
        Vec3 v = getDeltaMovement();
        double x = v.x;
        double y = v.y;
        double z = v.z;
        switch (face.getAxis()) {
            case X -> x = -x;
            case Y -> y = -y;
            case Z -> z = -z;
        }
        setDeltaMovement(x * BOUNCE_RETENTION, y * BOUNCE_RETENTION, z * BOUNCE_RETENTION);
    }

    @Override
    protected double getDefaultGravity() {
        return this.entityData.get(DATA_GRAVITY);
    }

    public float getDrag() {
        return this.entityData.get(DATA_DRAG);
    }

    private void emitTrail(ServerLevel level, Vec3 from, Vec3 to) {
        List<ParticleOptions> palette = profile.get(ShotComponents.PARTICLE_TRAIL).getParticles();
        int steps = (int) Math.round(from.distanceTo(to) * TRAIL_DENSITY);
        if (steps <= 0 || palette.isEmpty()) {
            return;
        }
        ClientboundBulletTrailPacket payload = new ClientboundBulletTrailPacket(from, to, palette);
        if (this.isRemoved()) {
            PacketDistributor.sendToPlayersTrackingChunk(level, this.chunkPosition(), payload);
        } else {
            PacketDistributor.sendToPlayersTrackingEntity(this, payload);
        }
    }
}
