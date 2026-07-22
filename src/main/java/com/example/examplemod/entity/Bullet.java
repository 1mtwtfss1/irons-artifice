package com.example.examplemod.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jspecify.annotations.NonNull;

public class Bullet extends Projectile {
    public Bullet(EntityType<? extends Bullet> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();
        Vec3 deltaMovement = getDeltaMovement();
        Vec3 position = position();
        Vec3 destination = position.add(deltaMovement);
        HitResult blockHitResult = level().clip(new ClipContext(position, destination, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        destination = blockHitResult.getLocation();
//        AABB collider = new AABB(position, destination).inflate(1);
        if (blockHitResult.getType() != HitResult.Type.MISS) {
            onHit(blockHitResult);
        }
        if (level().isClientSide()) {
            particleTrail(position, destination, 1);
        }
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9f));
        this.applyGravity();
        this.setPos(destination);
    }

    @Override
    protected void onHitBlock(@NonNull BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        discard();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    public void particleTrail(Vec3 a, Vec3 b, float density) {
        Vec3 delta = b.subtract(a).normalize();
        int steps = (int) (a.distanceTo(b) * density);
        float s = 0.3f;
        for (int i = 0; i < steps; i++) {
            Vec3 pos = a.lerp(b, (i + 1f) / steps);
            level().addAlwaysVisibleParticle(ParticleTypes.SMOKE, true, pos.x, pos.y, pos.z, delta.x * s, delta.y * s, delta.z * s);
        }
    }
}
