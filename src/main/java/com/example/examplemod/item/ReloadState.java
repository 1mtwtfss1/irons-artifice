package com.example.examplemod.item;

import com.example.examplemod.registry.DataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ReloadState(int tick, int duration) {
    public static final ReloadState EMPTY = new ReloadState(0, 0);

    public static final Codec<ReloadState> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("tick").forGetter(ReloadState::tick),
            Codec.INT.fieldOf("duration").forGetter(ReloadState::duration)
    ).apply(builder, ReloadState::new));

    public static final StreamCodec<ByteBuf, ReloadState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ReloadState::tick,
            ByteBufCodecs.VAR_INT, ReloadState::duration,
            ReloadState::new
    );

    public boolean isFinished() {
        return this.tick >= duration;
    }

    public float percent(float partialTick) {
        return (tick + partialTick) / duration;
    }

    public ReloadState increment(int ticks) {
        return new ReloadState(tick + ticks, duration);
    }

    /**
     * Manages {@link DataComponentRegistry#RELOAD_STATE}
     * @return whether reload has completed
     */
    public static boolean tickReload(ItemStack stack) {
        if (!stack.has(DataComponentRegistry.RELOAD_STATE)) {
            return true;
        }
        ReloadState state = stack.get(DataComponentRegistry.RELOAD_STATE);
        state = state.increment(1);
        if (state.isFinished()) {
            stack.remove(DataComponentRegistry.RELOAD_STATE);
            return true;
        }
        stack.set(DataComponentRegistry.RELOAD_STATE, state);
        return false;
    }
}
