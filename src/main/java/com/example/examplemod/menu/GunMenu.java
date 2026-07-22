package com.example.examplemod.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Hopper-layout menu (5 slots) for the gun item inventory.
 */
public class GunMenu extends AbstractContainerMenu {
    private final Container gunInventory;

    public GunMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(GunContainer.SIZE));
    }

    public GunMenu(int containerId, Inventory playerInventory, Container gunInventory) {
        super(MenuRegistry.GUN_MENU.get(), containerId);
        this.gunInventory = gunInventory;
        checkContainerSize(gunInventory, GunContainer.SIZE);
        gunInventory.startOpen(playerInventory.player);

        // Same slot positions as HopperMenu / hopper.png
        for (int x = 0; x < GunContainer.SIZE; x++) {
            this.addSlot(new Slot(gunInventory, x, 44 + x * 18, 20) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return gunInventory.canPlaceItem(this.index, stack);
                }
            });
        }

        this.addStandardInventorySlots(playerInventory, 8, 51);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.gunInventory.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (slotIndex < GunContainer.SIZE) {
                if (!this.moveItemStackTo(stack, GunContainer.SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, GunContainer.SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.gunInventory.stopOpen(player);
    }
}
