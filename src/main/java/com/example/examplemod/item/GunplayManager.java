package com.example.examplemod.item;

import com.example.examplemod.data.ReloadResult;
import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.entity.Bullet;
import com.example.examplemod.gun.GunProfile;
import com.example.examplemod.gun.ShotProfile;
import com.example.examplemod.menu.GunContainer;
import com.example.examplemod.modifier.ModifierItem;
import com.example.examplemod.network.ClientboundReloadCrosshairAnimationPacket;
import com.example.examplemod.recoil.RecoilState;
import com.example.examplemod.registry.EntityRegistry;
import com.example.examplemod.registry.ItemRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public final class GunplayManager {

    public static void tryFire(ServerPlayer player) {
        InteractionHand hand = InteractionHand.MAIN_HAND;
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof GunItem gunItem)) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(stack)) {
            return;
        }
        if (GunItem.isReloading(stack)) {
            return;
        }

        ServerLevel level = player.level();
        MagazineContents magazine = GunItem.getMagazine(stack);
        GunProfile gunProfile = gunItem.getGun();
        GunContainer modifiers = new GunContainer(stack);
        ShotProfile profile = compose(gunProfile, modifiers, player, stack, level);

        if (magazine.isEmpty()) {
            profile.get(ShotComponents.GUNSHOT_SOUND).playDryFireSound(level, player.position());
            player.getCooldowns().addCooldown(stack, 2);
            return;
        }

        long now = level.getGameTime();
        RecoilState offset = RecoilState.current(player, now);
        int roundIndex = gunProfile.magazineCapacity() - magazine.count();

        fireShot(level, player, profile, offset);
        GunItem.setMagazine(stack, magazine.deplete());
        profile.get(ShotComponents.GUNSHOT_SOUND).playGunShotSound(level, player.position());
        applyCharacterRecoil(player, profile);
        RecoilState.addImpulse(player, now, (float) profile.value(ShotComponents.CAMERA_RECOIL), roundIndex);
        player.getCooldowns().addCooldown(stack, (int) Math.round(profile.value(ShotComponents.FIRE_DELAY)));
    }

    private static void applyCharacterRecoil(ServerPlayer player, ShotProfile profile) {
        float strength = (float) profile.value(ShotComponents.CHARACTER_RECOIL);
        if (strength <= 0.0F) {
            return;
        }
        Vec3 look = player.getLookAngle();
        player.push(-look.x * strength, -look.y * strength * 0.5 + 0.05, -look.z * strength);
        player.hurtMarked = true;
    }

    private static void fireShot(ServerLevel level, ServerPlayer player, ShotProfile profile, RecoilState offset) {
        float pitch = player.getXRot() - offset.pitch();
        float yaw = player.getYRot() + offset.yaw();
        int projectileCount = Math.max(1, (int) Math.round(profile.value(ShotComponents.PROJECTILE_COUNT)));
        float speed = (float) profile.value(ShotComponents.BULLET_SPEED);
        float spread = getSpreadForEntity(profile, player);
        for (int i = 0; i < projectileCount; i++) {
            Bullet bullet = new Bullet(EntityRegistry.BULLET.get(), level);
            bullet.setOwner(player);
            bullet.applyProfile(profile);
            bullet.setPos(player.getEyePosition());
            bullet.shootFromRotation(
                    player,
                    pitch,
                    yaw,
                    0.0F,
                    speed,
                    spread
            );
            level.addFreshEntity(bullet);
        }
    }

    public static float getSpreadForEntity(ShotProfile shotProfile, Entity entity) {
        float crouchingMultiplier = 0.5f;
        float inAirMultiplier = 1.5f;
        float penaltyPerMovement = 3f;
        float maxMovementPenalty = 15f;

        float spread = (float) shotProfile.value(ShotComponents.SPREAD);
        if (entity.isCrouching()) {
            spread *= crouchingMultiplier;
        }
        if (!entity.onGround()) {
            spread *= inAirMultiplier;
        }
        float entitySpeed = (float) entity.getDeltaMovement().length();
        if (entitySpeed > 0.1) {
            float penalty = Mth.clamp(1 + penaltyPerMovement * entitySpeed, 1, maxMovementPenalty);
            spread *= penalty;
        }
        return spread;
    }

    public static ShotProfile compose(GunProfile gunProfile, Container modifiers, LivingEntity shooter,
                                      ItemStack gunStack, Level level) {
        ShotComponentMap components = gunProfile.baseProfile();
        for (int slot = 0; slot < modifiers.getContainerSize(); slot++) {
            ItemStack stack = modifiers.getItem(slot);
            if (stack.getItem() instanceof ModifierItem modifierItem) {
                modifierItem.getModifier().apply(components);
            }
        }
        return new ShotProfile(components);
    }

    public static ReloadResult attemptFinishReload(Player player, ItemStack gun) {
        // fixme: lots of duplicated checks with attemptStartReload
        if (!(gun.getItem() instanceof GunItem gunItem)) {
            return ReloadResult.NO_AMMO;
        }
        int capacity = gunItem.magazineCapacity();
        MagazineContents magazine = GunItem.getMagazine(gun);
        int missing = magazine.missing(capacity);
        if (missing <= 0) {
            return ReloadResult.ALREADY_FULL;
        }
        boolean requiresAmmo = !player.hasInfiniteMaterials();
        int available = countBullets(player);
        if (requiresAmmo && available <= 0) {
            return ReloadResult.NO_AMMO;
        }
        int toLoad = Math.min(missing, available);
        if (requiresAmmo) {
            consumeBullets(player, toLoad);
        } else {
            toLoad = missing;
        }
        GunItem.setMagazine(gun, magazine.with(magazine.count() + toLoad));
        return ReloadResult.FINISHED_RELOAD;
    }

    public static ReloadResult attemptStartReload(Player player, ItemStack gun) {
        if (!(gun.getItem() instanceof GunItem gunItem)) {
            return ReloadResult.NO_AMMO;
        }

        int capacity = gunItem.magazineCapacity();
        MagazineContents magazine = GunItem.getMagazine(gun);
        int missing = magazine.missing(capacity);
        if (missing <= 0) {
            return ReloadResult.ALREADY_FULL;
        }

        boolean requiresAmmo = !player.hasInfiniteMaterials();

        int available = countBullets(player);
        if (requiresAmmo && available <= 0) {
            return ReloadResult.NO_AMMO;
        }
        int ticks = gunItem.getGunProfile().reloadTimeTicks();
        GunItem.startReload(gun, ticks);
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new ClientboundReloadCrosshairAnimationPacket(ticks));
        }
        return ReloadResult.STARTING_RELOAD;
    }

    private static int countBullets(Player player) {
        Inventory inventory = player.getInventory();
        int total = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(ItemRegistry.BULLET.get())) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static void consumeBullets(Player player, int amount) {
        Inventory inventory = player.getInventory();
        int remaining = amount;
        for (int i = 0; i < inventory.getContainerSize() && remaining > 0; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(ItemRegistry.BULLET.get())) {
                int take = Math.min(remaining, stack.getCount());
                stack.shrink(take);
                remaining -= take;
            }
        }
    }
}
