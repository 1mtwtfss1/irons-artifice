package com.example.examplemod.gun;

import com.example.examplemod.data.PlayableSound;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class GunShotSoundStack {
    PlayableSound baseSound;
    PlayableSound dryFireSound;
    private final List<PlayableSound> accents = new ArrayList<>();

    public GunShotSoundStack(PlayableSound baseSound, PlayableSound dryFireSound) {
        this.baseSound = baseSound;
        this.dryFireSound = dryFireSound;
    }

    public void addAccent(PlayableSound options) {
        this.accents.add(options);
    }

    public void setBaseSound(PlayableSound sound) {
        this.baseSound = sound;
    }

    public List<PlayableSound> getAccentSounds() {
        return List.copyOf(accents);
    }

    public void playGunShotSound(Level level, Vec3 pos) {
        this.baseSound.play(level, pos, SoundSource.NEUTRAL);
        for (PlayableSound sound : accents) {
            sound.play(level, pos, SoundSource.NEUTRAL);
        }
    }

    public void playDryFireSound(Level level, Vec3 pos) {
        this.dryFireSound.play(level, pos, SoundSource.NEUTRAL);
    }
}
