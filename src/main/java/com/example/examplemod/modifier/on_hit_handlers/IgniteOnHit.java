package com.example.examplemod.modifier.on_hit_handlers;

import com.example.examplemod.entity.Bullet;
import com.example.examplemod.gun.OnHitEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class IgniteOnHit implements OnHitEffect {
    @Override
    public void onHit(ServerLevel level, Bullet bullet, HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHitResult) {
            entityHitResult.getEntity().igniteForSeconds(3);
        }
    }
}
