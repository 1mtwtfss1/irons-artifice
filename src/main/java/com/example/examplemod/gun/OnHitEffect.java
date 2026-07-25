package com.example.examplemod.gun;

import com.example.examplemod.entity.Bullet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public interface OnHitEffect {
    void onHit(ServerLevel level, Bullet bullet, HitResult hitResult);
}
