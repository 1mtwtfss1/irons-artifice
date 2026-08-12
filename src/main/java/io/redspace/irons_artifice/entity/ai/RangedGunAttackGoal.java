package io.redspace.irons_artifice.entity.ai;

import io.redspace.irons_artifice.item.FireDelayState;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.item.GunplayManager;
import io.redspace.irons_artifice.utils.Utils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RangedGunAttackGoal<T extends Mob> extends Goal {
    private final T mob;
    private LivingEntity target;
    private final float attackRangeSqr;
    private final float engageRangeSqr;
    private boolean hasLos;
    private int seeTime;

    public RangedGunAttackGoal(T mob, float range) {
        this.mob = mob;
        this.attackRangeSqr = range * range;
        float r = mob.getAttributes().hasAttribute(Attributes.FOLLOW_RANGE) ? (float) mob.getAttributeValue(Attributes.FOLLOW_RANGE) : range;
        this.engageRangeSqr = r * r;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    public RangedGunAttackGoal(T mob) {
        this(mob, 32);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            // defer to last target. helps when mob loses LOS
            target = this.target;
        } else {
            this.target = target;
        }
        return target != null && target.isAlive()
                && holdingGun()
                && mob.distanceToSqr(target) <= engageRangeSqr;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        super.start();
        mob.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        mob.setAggressive(false);
        this.mob.setTarget(null);
        this.target = null;
        this.seeTime = 0;
    }

    @Override
    public void tick() {
        if (target == null) {
            return;
        }
        if (mob.tickCount % 10 == 0) {
            this.hasLos = Utils.hasLineOfSight(mob, target);
        }
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        ItemStack gun = mob.getMainHandItem();
        if (!(gun.getItem() instanceof GunItem)) {
            return;
        }

        if (GunItem.isReloading(gun) || FireDelayState.isActive(gun)) {
            return;
        }

        if (GunItem.getMagazine(gun).isEmpty()) {
            GunplayManager.attemptStartReload(mob, gun);
            return;
        }
        Vec3 from = mob.getEyePosition();
        Vec3 to = target.getEyePosition();
        Vec3 direction = to.subtract(from);
        double distanceSqr = direction.lengthSqr();

        if (hasLos) {
            seeTime++;
        } else {
            seeTime = 0;
        }
        if (distanceSqr > attackRangeSqr || !hasLos) {
            if (mob.tickCount % 20 == 0) {
                this.mob.getNavigation().moveTo(target, 1f);
            }
        }
        if (seeTime > 20) {
            mob.setYRot(mob.yHeadRot);
            GunplayManager.tryFire(mob, direction.normalize());
            mob.setNoActionTime(0);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private boolean holdingGun() {
        return mob.getMainHandItem().getItem() instanceof GunItem;
    }
}
