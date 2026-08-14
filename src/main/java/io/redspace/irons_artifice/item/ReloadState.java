package io.redspace.irons_artifice.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.redspace.irons_artifice.gun.ReloadCueStack;
import io.redspace.irons_artifice.registry.DataComponentRegistry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public record ReloadState(double progress, double duration, double speed, boolean jumped, int topLoadCount) {
    public static final ReloadState EMPTY = new ReloadState(0, 0, 0, false, 0);

    //    public static final Codec<ReloadState> CODEC = RecordCodecBuilder.create(builder -> builder.group(
//            Codec.INT.fieldOf("progress").forGetter(ReloadState::progress),
//            Codec.INT.fieldOf("duration").forGetter(ReloadState::duration),
//            Codec.INT.optionalFieldOf("cue_index", 0).forGetter(ReloadState::cueIndex),
//            Codec.FLOAT.optionalFieldOf("pitch_multiplier", 1f).forGetter(ReloadState::pitchMultiplier)
//    ).apply(builder, ReloadState::new));
//
//    public static final StreamCodec<ByteBuf, ReloadState> STREAM_CODEC = StreamCodec.composite(
//            ByteBufCodecs.VAR_INT, ReloadState::progress,
//            ByteBufCodecs.VAR_INT, ReloadState::duration,
//            ByteBufCodecs.VAR_INT, ReloadState::cueIndex,
//            ByteBufCodecs.FLOAT, ReloadState::pitchMultiplier,
//            ReloadState::new
//    );
    public static final Codec<ReloadState> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.DOUBLE.fieldOf("progress").forGetter(ReloadState::progress),
            Codec.DOUBLE.fieldOf("duration").forGetter(ReloadState::duration),
            Codec.DOUBLE.fieldOf("speed").forGetter(ReloadState::speed),
            Codec.BOOL.fieldOf("jumped").forGetter(ReloadState::jumped),
            Codec.INT.fieldOf("topLoadCount").forGetter(ReloadState::topLoadCount)
    ).apply(builder, ReloadState::new));
    public static final StreamCodec<ByteBuf, ReloadState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ReloadState::progress,
            ByteBufCodecs.DOUBLE, ReloadState::duration,
            ByteBufCodecs.DOUBLE, ReloadState::speed,
            ByteBufCodecs.BOOL, ReloadState::jumped,
            ByteBufCodecs.VAR_INT, ReloadState::topLoadCount,
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
        return (float) ((progress + partialTick) / duration);
    }

//    public double animationProgressSeconds(GunProfile gunProfile) {
//        return progress;
//    }

    public ReloadState increment(int ticks) {
        return new ReloadState(progress + ticks * speed / 20.0, duration, speed, jumped, topLoadCount);
    }

//    public ReloadState withCueIndex(int cueIndex) {
//        return new ReloadState(progress, duration, cueIndex, pitchMultiplier);
//    }

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
        double previous = state.progress;
        state = state.increment(1);

        ReloadCueStack cues = gun.getReloadCues();
        cues.playCuesBetween(owner, owner.position(), SoundSource.PLAYERS, previous, state.progress, (float) ((state.speed + 2) / 3));
        if (state.topLoadCount > 0 && !state.jumped() && gun.getGunProfile().topLoadConfig() != null) {
            TopLoadConfig topLoadConfig = gun.getGunProfile().topLoadConfig();
            if (state.progress >= topLoadConfig.loopStart()) {
                double resumeFrom = topLoadConfig.loopEnd() - topLoadConfig.loopDuration() * (state.topLoadCount - 1);
                state = new ReloadState(resumeFrom, state.duration, state.speed, true, state.topLoadCount);
            }
        }
//        int nextCue = cues.playDueCues(owner, owner.position(), SoundSource.PLAYERS, state.percent(0), state.cueIndex(), state.pitchMultiplier);
//        state = state.withCueIndex(nextCue);
        if (state.isFinished()) {
            remove(stack);
            return true;
        }
        set(stack, state);
        return false;
    }
}