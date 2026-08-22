package io.redspace.irons_artifice.entity;

import io.redspace.irons_artifice.datagen.LoadoutLootProvider;
import io.redspace.irons_artifice.menu.GunContainer;
import io.redspace.irons_artifice.modifier.ModifierItem;
import io.redspace.irons_artifice.registry.ItemRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;

public class DrownedPirateHelper {

    public static Drowned createDrownedPirate(ServerLevel level) {
        Drowned drowned = new Drowned(EntityType.DROWNED, level);
        drowned.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.TRICORNE_HAT.get()));
        drowned.setDropChance(EquipmentSlot.HEAD, 0);
        drowned.setItemSlot(EquipmentSlot.MAINHAND, createLoadout(level));
        drowned.setDropChance(EquipmentSlot.MAINHAND, 0);
        // todo: custom loot
        return drowned;
    }

    public static ItemStack createLoadout(ServerLevel level) {
        List<ItemStack> guns = rollLootTable(level, LoadoutLootProvider.DROWNED_PIRATE_GUN);
        if (guns.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack gun = guns.get(0);
        List<ItemStack> modifiers = rollLootTable(level, LoadoutLootProvider.DROWNED_PIRATE_LOADOUT);
        GunContainer container = new GunContainer(gun);
        int slot = 0;
        for (ItemStack stack : modifiers) {
            if (slot >= container.getContainerSize()) {
                break;
            }
            if (!(stack.getItem() instanceof ModifierItem)) {
                continue;
            }
            container.setItem(slot++, stack.copyWithCount(1));
        }
        container.setChanged();
        return gun;
    }

    public static List<ItemStack> rollLootTable(ServerLevel level, ResourceKey<LootTable> lootTableResourceKey) {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(lootTableResourceKey);
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        return table.getRandomItems(params);
    }
}
