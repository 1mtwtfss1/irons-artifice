package io.redspace.irons_artifice.menu;

import io.redspace.irons_artifice.registry.MenuRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GunMenu extends AbstractContainerMenu {

    private final Container gunInventory;
    private final int size;

    public List<Slot> getModifierSlots() {
        return modifierSlots;
    }

    private final List<Slot> modifierSlots;

    public GunMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new GunContainer(playerInventory.getSelectedItem()));
    }

    public GunMenu(int containerId, Inventory playerInventory, Container gunInventory) {
        super(MenuRegistry.GUN_MENU.get(), containerId);
        this.gunInventory = gunInventory;
        this.modifierSlots = new ArrayList<>();
        gunInventory.startOpen(playerInventory.player);
        this.size = gunInventory.getContainerSize();

        for (int x = 0; x < size; x++) {
            int rows = (size - 1) / 5 + 1;
            int columns = Math.min(size, 5);
            int left = 39;
            int top = 15;
            int width = 98;
            int height = 44;
            modifierSlots.add(this.addSlot(new Slot(gunInventory, x,
                    left + (width - columns * 18) / 2 + (x % 5) * 18,
                    top + (height - rows * 18) / 2 + (x / 5) * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return gunInventory.canPlaceItem(this.index, stack);
                }
            }));
        }

        this.addStandardInventorySlots(playerInventory, 8, 77);
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
            if (slotIndex < size) {
                if (!this.moveItemStackTo(stack, size, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, size, false)) {
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
