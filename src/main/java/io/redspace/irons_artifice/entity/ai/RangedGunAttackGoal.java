package io.redspace.irons_artifice.entity.ai;

import io.redspace.irons_artifice.entity.IGunslingerMob;
import io.redspace.irons_artifice.data.FireMode;
import io.redspace.irons_artifice.gun.ShotProfile;
import io.redspace.irons_artifice.item.FireDelayState;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.item.GunplayManager;
import io.redspace.irons_artifice.item.ReloadState;
import io.redspace.irons_artifice.utils.Utils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Consumer;

public class RangedGunAttackGoal<T extends Mob> extends Goal {
    private enum ShootPhase {
        IDLE,
        TELEGRAPHING_VOLLEY,
        VOLLEY,
        CHARGING_BAYONET
    }

    private static final double CHARGE_CHASE_MULTIPLIER = 1.5;

    private final T mob;
    // todo: factor gun spread into effective range
    private AiGunRange bands;
    private final GunCombatMoveControl mover = new GunCombatMoveControl();
    private final float engageRangeSqr;
    private final int telegraphMin;
    private final int telegraphMax;
    private final int volleyIntervalMin;
    private final int volleyIntervalMax;
    private final double chargeSpeed;
    private final int chargeMaxTicks;
    private final int chargeCooldownTicks;

    private LivingEntity target;
    private boolean hasLos;
    private int seeTime;
    private int noSeeTime;
    private ShootPhase phase = ShootPhase.IDLE;
    private int telegraphRemaining;
    private int shotsRemaining;
    private int volleyCooldown;
    private int bayonetCooldown;
    private int chargeTicksRemaining;

    public RangedGunAttackGoal(T mob) {
        this(mob, 24, 15, 45, 40, 80);
    }

    public RangedGunAttackGoal(T mob, float range) {
        this(mob, range, 15, 45, 40, 80);
    }

    public RangedGunAttackGoal(T mob, float range, int telegraphMinTicks, int telegraphMaxTicks,
                               int volleyIntervalMin, int volleyIntervalMax) {
        this(mob, range, telegraphMinTicks, telegraphMaxTicks, volleyIntervalMin, volleyIntervalMax, 1.25, 40, 60);
    }

    public RangedGunAttackGoal(T mob, float range, int telegraphMinTicks, int telegraphMaxTicks,
                               int volleyIntervalMin, int volleyIntervalMax,
                               double chargeSpeed, int chargeMaxTicks, int chargeCooldownTicks) {
        this.mob = mob;
        this.bands = new AiGunRange(range);
        float follow = Math.max(mob.getAttributes().hasAttribute(Attributes.FOLLOW_RANGE)
                ? (float) mob.getAttributeValue(Attributes.FOLLOW_RANGE)
                : range, range);
        this.engageRangeSqr = Math.max(follow, range) * Math.max(follow, range);
        this.telegraphMin = Math.min(telegraphMinTicks, telegraphMaxTicks);
        this.telegraphMax = Math.max(telegraphMinTicks, telegraphMaxTicks);
        this.volleyIntervalMin = Math.min(volleyIntervalMin, volleyIntervalMax);
        this.volleyIntervalMax = Math.max(volleyIntervalMin, volleyIntervalMax);
        this.chargeSpeed = chargeSpeed;
        this.chargeMaxTicks = chargeMaxTicks;
        this.chargeCooldownTicks = chargeCooldownTicks;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    protected void runHook(Consumer<IGunslingerMob> hook) {
        if (mob instanceof IGunslingerMob gunslingerMob) {
            hook.accept(gunslingerMob);
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity next = mob.getTarget();
        if (next == null) {
            next = this.target;
        } else {
            this.target = next;
        }
        if (noSeeTime > 200) {
            this.target = null;
            return false;
        }
        return next != null && next.isAlive() && mob.canAttack(next)
                && isHoldingGun()
                && mob.distanceToSqr(next) <= engageRangeSqr;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        mob.setAggressive(true);
    }

    @Override
    public void stop() {
        if (mob.isUsingItem()) {
            mob.stopUsingItem();
        }
        mob.setAggressive(false);
        mob.setTarget(null);
        target = null;
        seeTime = 0;
        noSeeTime = 0;
        phase = ShootPhase.IDLE;
        telegraphRemaining = 0;
        shotsRemaining = 0;
        volleyCooldown = 0;
        bayonetCooldown = 0;
        chargeTicksRemaining = 0;
        mover.reset();
        mob.getNavigation().stop();
        mob.getMoveControl().strafe(0, 0);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (target == null || !target.isAlive()) {
            return;
        }

        ItemStack gun = mob.getMainHandItem();
        if (!(gun.getItem() instanceof GunItem gunItem)) {
            return;
        }
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        hasLos = mob.getSensing().hasLineOfSight(target);
        if (hasLos) {
            seeTime++;
            noSeeTime = 0;
        } else {
            seeTime = 0;
            noSeeTime++;
        }

        double distSqr = mob.distanceToSqr(target);

        switch (phase) {
            case IDLE -> {
                if (volleyCooldown > 0) {
                    volleyCooldown--;
                }
                if (bayonetCooldown > 0) {
                    bayonetCooldown--;
                }
                if (canStartBayonetCharge(gun, distSqr)) {
                    beginBayonetCharge(gun);
                } else if (canStartVolley(gun, distSqr)) {
                    runHook(IGunslingerMob::onVolleyStart);
                    phase = ShootPhase.TELEGRAPHING_VOLLEY;
                    telegraphRemaining = randomBetween(telegraphMin, telegraphMax);
                } else if (GunItem.getMagazine(gun).isEmpty() && !GunItem.isReloading(gun)) {
                    GunplayManager.attemptStartReload(mob, gun);
                }
            }
            case TELEGRAPHING_VOLLEY -> {
                if (canStartBayonetCharge(gun, distSqr)) {
                    beginBayonetCharge(gun);
                    break;
                }
                if (shouldCancelVolley(distSqr)) {
                    phase = ShootPhase.IDLE;
                    break;
                }
                if (--telegraphRemaining <= 0) {
                    ShotProfile profile = GunplayManager.compose(mob, gunItem.getGun(), gun);
                    shotsRemaining = rollVolleyShots(gun, gunItem, profile, mob.getRandom());
                    phase = ShootPhase.VOLLEY;
                }
            }
            case VOLLEY -> {
                if (canStartBayonetCharge(gun, distSqr)) {
                    endVolley();
                    beginBayonetCharge(gun);
                    break;
                }
                if (shouldCancelVolley(distSqr)) {
                    endVolley();
                    break;
                }
                if (shotsRemaining > 0 && !FireDelayState.isActive(gun) && !GunItem.isReloading(gun)
                        && !GunItem.getMagazine(gun).isEmpty()) {
                    Vec3 aim = target.getEyePosition().subtract(mob.getEyePosition());
                    if (aim.lengthSqr() > 1.0E-6 && GunplayManager.tryFire(mob, aim.normalize())) {
                        shotsRemaining--;
                        mob.setNoActionTime(0);
                    }
                }
                if (shotsRemaining <= 0 || GunItem.getMagazine(gun).isEmpty()) {
                    endVolley();
                    if (GunItem.getMagazine(gun).isEmpty() && !GunItem.isReloading(gun)) {
                        GunplayManager.attemptStartReload(mob, gun);
                    }
                }
            }
            case CHARGING_BAYONET -> {
                if (shouldEndBayonetCharge(distSqr)) {
                    endBayonetCharge();
                }
            }
        }

        GunCombatMoveControl.PositionMode mode = switch (phase) {
            case IDLE -> mover.selectKiting(distSqr, hasLos, bands);
            case CHARGING_BAYONET -> GunCombatMoveControl.PositionMode.CHARGE;
            case TELEGRAPHING_VOLLEY, VOLLEY -> GunCombatMoveControl.PositionMode.PLANT;
        };
        mover.tick(mob, target, bands, mode, chargeSpeed);
    }

    private void beginBayonetCharge(ItemStack gun) {
        Utils.spawnParticles(mob.level(), ParticleTypes.ANGRY_VILLAGER, mob.getX(), mob.getY() + 2, mob.getZ(), 25, 0, 0, 0, 0, true);
        if (GunItem.isReloading(gun)) {
            ReloadState.remove(gun);
        }
        if (mob.isUsingItem()) {
            mob.stopUsingItem();
        }
        mob.startUsingItem(InteractionHand.MAIN_HAND);
        phase = ShootPhase.CHARGING_BAYONET;
        chargeTicksRemaining = chargeMaxTicks;
    }

    private void endBayonetCharge() {
        if (mob.isUsingItem()) {
            mob.stopUsingItem();
        }
        phase = ShootPhase.IDLE;
        chargeTicksRemaining = 0;
        bayonetCooldown = chargeCooldownTicks;
    }

    private boolean canStartBayonetCharge(ItemStack gun, double distSqr) {
        return bayonetCooldown <= 0
                && distSqr < bands.bayonetSqr()
                && gun.has(DataComponents.KINETIC_WEAPON);
    }

    private boolean shouldEndBayonetCharge(double distSqr) {
        if (!target.isAlive()) {
            return true;
        }
        if (--chargeTicksRemaining <= 0) {
            return true;
        }
        float chaseRange = bands.bayonetRange() * (float) CHARGE_CHASE_MULTIPLIER;
        if (distSqr > chaseRange * chaseRange) {
            return true;
        }
        return mob.stabbedEntities(e -> e == target) > 0;
    }

    private void endVolley() {
        phase = ShootPhase.IDLE;
        shotsRemaining = 0;
        volleyCooldown = randomBetween(volleyIntervalMin, volleyIntervalMax);
        runHook(IGunslingerMob::onVolleyEnd);
    }

    private boolean canStartVolley(ItemStack gun, double distSqr) {
        return volleyCooldown <= 0
                && hasLos
                && seeTime >= 20
                && distSqr <= bands.maxRangeSqr()
                && distSqr >= bands.panicSqr()
                && !GunItem.isReloading(gun)
                && !GunItem.getMagazine(gun).isEmpty();
    }

    private boolean shouldCancelVolley(double distSqr) {
        return !target.isAlive() || distSqr < bands.panicSqr() || noSeeTime > 40;
    }

    private int rollVolleyShots(ItemStack gun, GunItem gunItem, ShotProfile profile, RandomSource random) {
        int mag = GunItem.getMagazine(gun).count();
        int capacity = Math.max(1, gunItem.magazineCapacity());
        float minFrac = 0.20f;
        float maxFrac = 0.40f;
        if (profile.fireMode() == FireMode.AUTO) {
            minFrac *= 2;
            maxFrac *= 2;
        }
        float frac = Mth.lerp(random.nextFloat(), minFrac, maxFrac);
        int fromPercent = Math.max(1, Math.round(capacity * frac));
        return Math.min(mag, fromPercent);
    }

    private int randomBetween(int min, int max) {
        return Mth.randomBetweenInclusive(mob.getRandom(), min, max);
    }

    private boolean isHoldingGun() {
        return mob.getMainHandItem().getItem() instanceof GunItem;
    }
}
