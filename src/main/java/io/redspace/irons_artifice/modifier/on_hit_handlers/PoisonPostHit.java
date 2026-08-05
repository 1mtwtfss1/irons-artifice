package io.redspace.irons_artifice.modifier.on_hit_handlers;

import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.gun.PostHitEffect;
import io.redspace.irons_artifice.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;

public class PoisonPostHit implements PostHitEffect {
    private int durationTicks;

    public PoisonPostHit(int durationTicks) {
        this.durationTicks = durationTicks;
    }

    public void addDuration(int ticks) {
        this.durationTicks += ticks;
    }

    public int getDurationTicks() {
        return durationTicks;
    }

    @Override
    public void postHit(ServerLevel level, Bullet bullet, HitResult hitResult, Entity entity) {
        if (!(entity instanceof LivingEntity living) || !Utils.canHarm(bullet.getOwner(), entity)) {
            return;
        }
        MobEffectInstance existing = living.getEffect(MobEffects.POISON);
        int base = existing != null ? existing.getDuration() : 0;
        living.addEffect(new MobEffectInstance(MobEffects.POISON, base + durationTicks, 0), bullet.getOwner());
    }
}
