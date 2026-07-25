package com.example.examplemod.entity;

import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.gun.BlockDamageManager;
import com.example.examplemod.gun.OnHitEffect;
import com.example.examplemod.gun.OnHitEffects;
import com.example.examplemod.gun.ShotProfile;
import com.example.examplemod.network.ClientboundBulletImpactPacket;
import com.example.examplemod.network.ClientboundBulletTrailPacket;
import com.example.examplemod.utils.Utils;
import net.minecraft.core.BlockPos;
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

    public static final double TRAIL_DENSITY = 2.0;

    @Nullable
    private ShotProfile profile;
    private int piercingRemaining = 0;
    private final Set<Integer> piercedEntities = new HashSet<>();
    private HitState hitState = HitState.CONTINUE;

    public Bullet(EntityType<? extends Bullet> type, Level level) {
        super(type, level);
    }

    enum HitState {
        CONTINUE,
        STOP,
        DISCARD;
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
    protected boolean canHitEntity(@NonNull Entity entity) {
        return super.canHitEntity(entity) && !piercedEntities.contains(entity.getId());
    }

    public float resolveDamage() {
        if (profile == null) {
            return 0;
        }
        // fixme: this is dumb
        return (float) (profile.value(ShotComponents.DAMAGE) / Math.max(1, profile.value(ShotComponents.PROJECTILE_COUNT)));
    }

    @Override
    public void tick() {
        super.tick();
        this.piercedEntities.clear();

        Vec3 delta = getDeltaMovement();
        Vec3 position = position();
        Vec3 destination = position.add(delta);

        int i = 64;
        this.hitState = HitState.CONTINUE;
        while (hitState == HitState.CONTINUE && --i > 0) {
            HitResult blockHit = level().clip(new ClipContext(
                    position, destination, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
            Vec3 blockClip = blockHit.getLocation();
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                    level(), this, position, blockClip,
                    getBoundingBox().expandTowards(delta).inflate(1),
                    this::canHitEntity, 0.25f);
            if (entityHit != null) {
                onHit(entityHit);
                destination = entityHit.getLocation();
            } else if (blockHit.getType() != HitResult.Type.MISS) {
                onHit(blockHit);
                destination = blockHit.getLocation();
            }
            if (hitState == HitState.STOP) {
                break;
            } else if (hitState == HitState.DISCARD) {
                discard();
                break;
            }
        }

        if (level() instanceof ServerLevel serverLevel && profile != null) {
            Vec3 particleStart = this.tickCount == 1 ? position.subtract(0, 0.25, 0) : position;
            emitTrail(serverLevel, particleStart, destination);
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
        hitState = HitState.DISCARD; // setup default hit state
        super.onHit(hitResult);
        if (profile == null) {
            return;
        }
        if (level() instanceof ServerLevel serverLevel) {
            OnHitEffects onHitEffects = profile.get(ShotComponents.ON_HIT);
            for (OnHitEffect effect : onHitEffects.all()) {
                effect.onHit(serverLevel, this, hitResult);
            }
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
        float damage = resolveDamage();
        DamageSource source = damageSources().mobProjectile(this, owner instanceof LivingEntity le ? le : null);
        target.hurtServer(serverLevel, source, damage);

        float knockback = (float) profile.value(ShotComponents.KNOCKBACK);
        if (target instanceof LivingEntity living && knockback > 0.0F) {
            Vec3 v = getDeltaMovement();
            living.knockback(knockback, -v.x, -v.z);
        }

        if (piercingRemaining > 0) {
            this.hitState = HitState.CONTINUE;
            piercingRemaining--;
        }
    }

    protected void playBlockHitEffects(BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        level().playSound(null, pos, level().getBlockState(pos).getSoundType(level(), pos, null).getBreakSound(), SoundSource.BLOCKS, .75f, 1f);
        if (level() instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, this.chunkPosition(), new ClientboundBulletImpactPacket(hitResult.getLocation(), this.getDeltaMovement(), hitResult.getDirection().getUnitVec3()));
        }
    }

    /**
     * @return whether the block was completed destroyed
     */
    protected boolean attemptApplyBlockDamage(BlockHitResult hitResult) {
        if (level() instanceof ServerLevel serverLevel && this.profile != null && profile.get(ShotComponents.BREAKS_BLOCKS)) {
            float damage = (float) (this.profile.value(ShotComponents.BLOCK_DAMAGE_MULTIPLIER) * resolveDamage());
            return BlockDamageManager.applyDamage(serverLevel, hitResult.getBlockPos(), level().getBlockState(hitResult.getBlockPos()), damage, this);
        }
        return false;
    }

    @Override
    protected void onHitBlock(@NonNull BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        playBlockHitEffects(hitResult);
        if (profile == null) {
            return;
        }
        boolean brokeThroughBlock = attemptApplyBlockDamage(hitResult);
        if (brokeThroughBlock) {
            // if we break through block, continue forward, and maybe pierce for additional buffs
            hitState = HitState.CONTINUE;
            if (piercingRemaining > 0) {
                piercingRemaining--;
            } else {
                // allow the bullet to continue without piercing, but do not allow additional block damage thereafter
                profile.remove(ShotComponents.BREAKS_BLOCKS);
            }
        } else {
            // on solid block impact, ricochet or discard
            int ricochet = this.entityData.get(DATA_RICOCHET);
            if (ricochet > 0) {
                this.entityData.set(DATA_RICOCHET, ricochet - 1);
                reflectMotion(hitResult.getDirection());
                hitState = HitState.STOP; // stop in place and resume next tick, raycaster doesn't handle bent segments
            } else {
                hitState = HitState.DISCARD;
            }
        }
    }

    private void reflectMotion(Direction face) {
        setDeltaMovement(Utils.reflect(getDeltaMovement(), face.getUnitVec3()));
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
