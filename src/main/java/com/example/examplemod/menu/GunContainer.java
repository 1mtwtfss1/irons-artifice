package com.example.examplemod.menu;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class GunContainer extends SimpleContainer {
    public static final int SIZE = 5;

    private final ItemStack stack;

    public GunContainer(ItemStack stack) {
        super(SIZE);
        this.stack = stack;
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(this.getItems());
    }

    public ItemStack getGunStack() {
        return this.stack;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
    }

    @Override
    public boolean stillValid(Player player) {
        return !player.isSpectator()
                && (player.getMainHandItem() == this.stack || player.getOffhandItem() == this.stack);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        // Only modifier items belong in a gun's modifier slots.
        return stack.getItem() instanceof com.example.examplemod.modifier.ModifierItem;
    }
}
