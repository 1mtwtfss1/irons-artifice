package com.example.examplemod.item;

import com.example.examplemod.gun.GunProfile;
import com.example.examplemod.gun.ReloadCueStack;
import com.example.examplemod.registry.DataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public record ReloadState(int progress, int duration, int cueIndex, float pitchMultiplier) {
    public static final ReloadState EMPTY = new ReloadState(0, 0, 0, 1);

    public static final Codec<ReloadState> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("progress").forGetter(ReloadState::progress),
            Codec.INT.fieldOf("duration").forGetter(ReloadState::duration),
            Codec.INT.optionalFieldOf("cue_index", 0).forGetter(ReloadState::cueIndex),
            Codec.FLOAT.optionalFieldOf("pitch_multiplier", 1f).forGetter(ReloadState::pitchMultiplier)
    ).apply(builder, ReloadState::new));

    public static final StreamCodec<ByteBuf, ReloadState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ReloadState::progress,
            ByteBufCodecs.VAR_INT, ReloadState::duration,
            ByteBufCodecs.VAR_INT, ReloadState::cueIndex,
            ByteBufCodecs.FLOAT, ReloadState::pitchMultiplier,
            ReloadState::new
    );

    public static @Nullable ReloadState get(ItemStack stack) {
        return stack.get(DataComponentRegistry.RELOAD_STATE);
    }

    public static void set(ItemStack stack, ReloadState state) {
        stack.set(DataComponentRegistry.RELOAD_STATE, state);
    }

    public static boolean has(ItemStack stack) {
        return stack.has(DataComponentRegistry.RELOAD_STATE);
    }

    public static void remove(ItemStack stack) {
        stack.remove(DataComponentRegistry.RELOAD_STATE);
    }

    public boolean isFinished() {
        return this.progress >= duration;
    }

    public float percent(float partialTick) {
        if (duration <= 0) {
            return 1f;
        }
        return (progress + partialTick) / duration;
    }

    public double animationProgressSeconds(GunProfile gunProfile) {
        // calculate based on percent completed to animation time (base reload time) to avoid multipliers affecting pausing the animation at the wrong time
        return this.percent(0) * gunProfile.reloadTimeTicks() / 20.0;
    }

    public ReloadState increment(int ticks) {
        return new ReloadState(progress + ticks, duration, cueIndex, pitchMultiplier);
    }

    public ReloadState withCueIndex(int cueIndex) {
        return new ReloadState(progress, duration, cueIndex, pitchMultiplier);
    }

    /**
     * Advances reload progress and plays due sound cues.
     *
     * @return whether reload has completed
     */
    public static boolean tickReload(ItemStack stack, GunItem gun, Level level, Entity owner) {
        ReloadState state = get(stack);
        if (state == null) {
            return true;
        }
        state = state.increment(1);

        ReloadCueStack cues = gun.getReloadCues();
        int nextCue = cues.playDueCues(level, owner.position(), SoundSource.PLAYERS, state.percent(0), state.cueIndex(), state.pitchMultiplier);
        state = state.withCueIndex(nextCue);

        if (state.isFinished()) {
            remove(stack);
            return true;
        }
        set(stack, state);
        return false;
    }
}
