package com.example.examplemod.item;

import com.example.examplemod.data.ReloadResult;
import com.example.examplemod.gun.GunProfile;
import com.example.examplemod.registry.DataComponentRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GunItem extends Item {
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

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            ReloadResult result = GunplayManager.reload(player, stack);
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
            case LOADED -> level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_LOADING_END.value(), SoundSource.PLAYERS, 0.8F, 1.0F);
            case ALREADY_FULL, NO_AMMO -> level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.6F, 1.0F);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int count = getMagazine(stack).count();
        return Mth.clamp(Math.round(count * 13.0F / magazineCapacity()), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // hell yeah
        return 0xFFAA00;
    }
}
