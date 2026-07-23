package com.example.examplemod.item;

import com.example.examplemod.data.ReloadResult;
import com.example.examplemod.data.ShotComponentMap;
import com.example.examplemod.data.ShotComponents;
import com.example.examplemod.entity.Bullet;
import com.example.examplemod.registry.EntityRegistry;
import com.example.examplemod.gun.GunProfile;
import com.example.examplemod.gun.ShotProfile;
import com.example.examplemod.menu.GunContainer;
import com.example.examplemod.modifier.ModifierItem;
import com.example.examplemod.recoil.RecoilState;
import com.example.examplemod.registry.ItemRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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

        ServerLevel level = (ServerLevel) player.level();
        MagazineContents magazine = GunItem.getMagazine(stack);
        GunProfile gunProfile = gunItem.getGun();
        GunContainer modifiers = new GunContainer(stack);
        ShotProfile profile = compose(gunProfile, modifiers, player, stack, level);

        if (magazine.isEmpty()) {
            profile.get(ShotComponents.GUNSHOT_SOUND).playDryFireSound(level, player.position());
            player.getCooldowns().addCooldown(stack, 2);
            return;
        }

        // Fire along the current recoil offset so bullets track the recoiled crosshair, not raw aim.
        long now = level.getGameTime();
        RecoilState offset = RecoilState.current(player, now);
        int roundIndex = gunProfile.magazineCapacity() - magazine.count();

        fireShot(level, player, profile, offset);
        GunItem.setMagazine(stack, magazine.deplete());
        profile.get(ShotComponents.GUNSHOT_SOUND).playGunShotSound(level, player.position());
        applyCharacterRecoil(player, profile);
        RecoilState.addImpulse(player, now, (float) profile.value(ShotComponents.CAMERA_RECOIL), roundIndex);
        player.getCooldowns().addCooldown(stack, gunProfile.fireCooldownTicks());
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
        float spread = (float) profile.value(ShotComponents.SPREAD);
        for (int i = 0; i < projectileCount; i++) {
            Bullet bullet = new Bullet(EntityRegistry.BULLET.get(), level);
            bullet.setOwner(player);
            bullet.applyProfile(profile);
            bullet.setPos(player.getEyePosition().subtract(0, 0.25, 0));
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

    public static ReloadResult reload(Player player, ItemStack gun) {
        if (!(gun.getItem() instanceof GunItem gunItem)) {
            return ReloadResult.NO_AMMO;
        }

        int capacity = gunItem.magazineCapacity();
        MagazineContents magazine = GunItem.getMagazine(gun);
        int missing = magazine.missing(capacity);
        if (missing <= 0) {
            return ReloadResult.ALREADY_FULL;
        }

        if (player.hasInfiniteMaterials()) {
            GunItem.setMagazine(gun, magazine.with(capacity));
            return ReloadResult.LOADED;
        }

        int available = countBullets(player);
        if (available <= 0) {
            return ReloadResult.NO_AMMO;
        }

        int toLoad = Math.min(missing, available);
        consumeBullets(player, toLoad);
        GunItem.setMagazine(gun, magazine.with(magazine.count() + toLoad));
        return ReloadResult.LOADED;
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
