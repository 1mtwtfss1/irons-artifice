package com.example.examplemod.gun;

import com.example.examplemod.data.PlayableSound;
import com.example.examplemod.network.ClientboundLocalSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ReloadCueStack {
    public static final ReloadCueStack EMPTY = new ReloadCueStack(List.of());

    private final List<ReloadCue> cues;

    private ReloadCueStack(List<ReloadCue> cues) {
        this.cues = List.copyOf(cues);
    }

    public static ReloadCueStack of(ReloadCue... cues) {
        List<ReloadCue> sorted = Arrays.stream(cues)
                .sorted(Comparator.comparingDouble(ReloadCue::percent))
                .toList();
        return new ReloadCueStack(sorted);
    }

    public static ReloadCueStack of(PlayableSound sound, float... percents) {
        ReloadCue[] cues = new ReloadCue[percents.length];
        for (int i = 0; i < percents.length; i++) {
            cues[i] = new ReloadCue(percents[i], sound);
        }
        return of(cues);
    }

    public int size() {
        return cues.size();
    }

    public boolean isEmpty() {
        return cues.isEmpty();
    }

    public ReloadCue get(int index) {
        return cues.get(index);
    }

    /**
     * Plays any cues crossed while advancing to {@code percent}, starting at {@code cueIndex}.
     *
     * @return the next cue index to fire
     */
    public int playDueCues(Entity owner, Vec3 pos, SoundSource source, float percent, int cueIndex, float pitchMultiplier) {
        while (cueIndex < cues.size() && percent >= cues.get(cueIndex).percent()) {
            var sound = cues.get(cueIndex).sound();
            var adjustedSound = new PlayableSound(sound.soundEventHolder(), sound.volume(), sound.minPitch() * pitchMultiplier, sound.maxPitch() * pitchMultiplier);
            owner.level().playSound(owner, pos.x, pos.y, pos.z, adjustedSound.soundEventHolder().value(), source, adjustedSound.volume(), adjustedSound.samplePitch(owner.getRandom()));
            if (owner instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new ClientboundLocalSoundPacket(source, adjustedSound));
            }
            cueIndex++;
        }
        return cueIndex;
    }
}
