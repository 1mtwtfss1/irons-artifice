package io.redspace.irons_artifice.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue ILLIFICER_REPLACE_PATROL_LEADER_CHANCE = BUILDER
            .push("illificer")
            .comment("Chance (0-1) for an Illificer to replace an Patrol Leader on natural patrol")
            .defineInRange("replacePatrolLeader", 0.50, 0.0, 1.0);

    public static final ModConfigSpec SPEC = BUILDER.pop().build();
}
