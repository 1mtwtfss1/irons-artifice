package com.example.examplemod.item;

import com.example.examplemod.data.ReloadResult;
import com.example.examplemod.gun.GunProfile;
import com.example.examplemod.registry.DataComponentRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class GunItem extends BaseGeoItem {
    private final GunProfile gunProfile;

    public GunItem(Properties properties, GunProfile gunProfile) {
        super(properties);
        this.gunProfile = gunProfile;
    }

    public GunProfile getGun() {
        return gunProfile;
    }

    public int magazineCapacity() {
        return gunProfile.magazineCapacity();
    }

    public static MagazineContents getMagazine(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.MAGAZINE.get(), MagazineContents.EMPTY);
    }

    public static void setMagazine(ItemStack stack, MagazineContents magazine) {
        stack.set(DataComponentRegistry.MAGAZINE.get(), magazine);
    }

    public static void startReload(ItemStack stack, int duration) {
        stack.set(DataComponentRegistry.RELOAD_STATE, new ReloadState(0, duration));
    }

    public static boolean isReloading(ItemStack stack) {
        return stack.has(DataComponentRegistry.RELOAD_STATE);
    }

    public GunProfile getGunProfile() {
        return gunProfile;
    }

    @Override
    public void inventoryTick(@NonNull ItemStack itemStack, @NonNull ServerLevel level, @NonNull Entity owner, @Nullable EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, owner, slot);
        if (slot != EquipmentSlot.MAINHAND || !(owner instanceof Player player)) {
            // fixme: will this cause issue in offhand? i think a lot of things (animations, dual-wielding) need specific offhand handling
            //  not a v1 concern
            return;
        }
        if (isReloading(itemStack) && ReloadState.tickReload(itemStack)) {
            ReloadResult result = GunplayManager.attemptFinishReload(player, itemStack);
            playReloadFeedback(level, player, result);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && !isReloading(stack)) {
            ReloadResult result = GunplayManager.attemptStartReload(player, stack);
            playReloadFeedback(level, player, result);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    private static void playReloadFeedback(Level level, Player player, ReloadResult result) {
        switch (result) {
            case FINISHED_RELOAD -> level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_LOADING_END.value(), SoundSource.PLAYERS, 0.8F, 1.0F);
            case STARTING_RELOAD -> level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_LOADING_START.value(), SoundSource.PLAYERS, 0.8F, 1.0F);
            case NO_AMMO -> level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.6F, 1.0F);
            case ALREADY_FULL -> level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.NOTE_BLOCK_DIDGERIDOO, SoundSource.PLAYERS, 0.6F, 1.0F);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (isReloading(stack)) {
            return (int) (stack.get(DataComponentRegistry.RELOAD_STATE).percent(0) * 13);
        } else {
            int count = getMagazine(stack).count();
            return Mth.clamp(Math.round(count * 13.0F / magazineCapacity()), 0, 13);
        }
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (isReloading(stack)) {
            return 0xAAAAAA;
        } else {
            // hell yeah
            return 0xFFAA00;
        }
    }
}
