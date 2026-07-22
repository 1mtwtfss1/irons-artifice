package com.example.examplemod.item;

import com.example.examplemod.entity.Bullet;
import com.example.examplemod.entity.EntityRegistry;
import com.example.examplemod.menu.GunContainer;
import com.example.examplemod.menu.GunMenu;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class GunItem extends Item {
    public GunItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isSecondaryUseActive()) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> new GunMenu(containerId, inventory, new GunContainer(stack)),
                        Component.translatable("container.examplemod.gun")
                ));
            }
            return InteractionResult.SUCCESS;
        }

        if (level instanceof ServerLevel serverLevel) {
            Bullet arrow = new Bullet(EntityRegistry.BULLET.get(), serverLevel);
            arrow.setPos(player.getEyePosition());
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 30F, 1.0F);
            serverLevel.addFreshEntity(arrow);

            serverLevel.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.ARROW_SHOOT,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F
            );
        }

        return InteractionResult.SUCCESS;
    }
}
