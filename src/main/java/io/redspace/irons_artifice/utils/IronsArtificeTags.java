package io.redspace.irons_artifice.utils;

import io.redspace.irons_artifice.IronsArtifice;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class IronsArtificeTags {
    public static final TagKey<Block> ALWAYS_BREAK = tag("always_break");
    public static final TagKey<Block> NEVER_BREAK = tag("never_break");

    private static TagKey<Block> tag(String path) {
        return TagKey.create(Registries.BLOCK, IronsArtifice.id(path));
    }
}
