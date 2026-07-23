package com.example.examplemod.entity;

import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.gun.OnHitEffect;
import com.example.examplemod.gun.ShotProfile;
import com.example.examplemod.network.ClientboundBulletTrailPacket;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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

    private static final float DRAG = 0.99F;
    private static final double BOUNCE_RETENTION = 0.8;
    // todo: not hardcode ignite
    private static final float IGNITE_SECONDS = 5.0F;

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
        this.piercingRemaining = (int) Math.round(profile.value(ShotComponents.PIERCING));
        // Gravity and ricochet are synced so the client's movement/bounces track the server.
        this.entityData.set(DATA_GRAVITY, (float) profile.value(ShotComponents.GRAVITY));
        this.entityData.set(DATA_RICOCHET, (int) Math.round(profile.value(ShotComponents.RICOCHET)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_GRAVITY, 0.05F);
        builder.define(DATA_RICOCHET, 0);
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

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                level(), this, position, destination,
                getBoundingBox().expandTowards(delta).inflate(0.3),
                this::canHitEntity);

        if (entityHit != null) {
            onHit(entityHit);
        } else if (blockHit.getType() != HitResult.Type.MISS) {
            onHit(blockHit);
        }

        if (level() instanceof ServerLevel serverLevel && profile != null) {
            emitTrail(serverLevel, position, destination);
        }

        this.setDeltaMovement(getDeltaMovement().scale(DRAG));
        this.applyGravity();
        this.setPos(destination);
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
        DamageSource source = damageSources().mobProjectile(this, owner instanceof LivingEntity le ? le : null);
        target.hurtServer(serverLevel, source, (float) profile.value(ShotComponents.DAMAGE));

        if (profile.get(ShotComponents.ON_HIT).contains(OnHitEffect.IGNITE)) {
            target.igniteForSeconds(IGNITE_SECONDS);
        }
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
        Vec3 pos = hitResult.getLocation().subtract(this.getDeltaMovement().normalize().scale(0.05));
        level().addParticle(ParticleTypes.FLAME, true, true, pos.x, pos.y, pos.z, 0, 0, 0);
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
