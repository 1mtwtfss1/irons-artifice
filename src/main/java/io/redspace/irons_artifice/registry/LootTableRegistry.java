package io.redspace.irons_artifice.registry;

import io.redspace.irons_artifice.IronsArtifice;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public final class LootTableRegistry {
    public static final ResourceKey<LootTable> ILLIFICER_LOADOUT = key("loadouts/illificer/loadout");
    public static final ResourceKey<LootTable> ILLIFICER_MAIN_MODIFIER = key("loadouts/illificer/main_modifier");
    public static final ResourceKey<LootTable> ILLIFICER_AUX_MODIFIER = key("loadouts/illificer/aux_modifier");

    private static ResourceKey<LootTable> key(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, IronsArtifice.id(path));
    }
}
