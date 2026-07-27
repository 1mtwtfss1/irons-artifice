package com.example.examplemod.gun;

import com.example.examplemod.data.PlayableSound;
import com.example.examplemod.network.ClientboundGunshotEchoPacket;
import com.example.examplemod.registry.SoundRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class GunShotSoundStack {
    PlayableSound baseSound;
    PlayableSound dryFireSound;
    PlayableSound echoSound;
    private final List<PlayableSound> accents = new ArrayList<>();

    public GunShotSoundStack(PlayableSound baseSound, PlayableSound dryFireSound, PlayableSound echoSound) {
        this.baseSound = baseSound;
        this.dryFireSound = dryFireSound;
        this.echoSound = echoSound;
    }

    public GunShotSoundStack(PlayableSound baseSound, PlayableSound dryFireSound) {
        this(baseSound, dryFireSound, PlayableSound.standard(SoundRegistry.GENERIC_BULLET_ECHO));
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
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, null, pos.x, pos.y, pos.z, 12 * 16,
                    new ClientboundGunshotEchoPacket(this.echoSound.soundEventHolder(), SoundSource.NEUTRAL, pos.x, pos.y, pos.z, this.echoSound.minPitch(), this.echoSound.maxPitch()));
        }
    }

    public void playDryFireSound(Level level, Vec3 pos) {
        this.dryFireSound.play(level, pos, SoundSource.NEUTRAL);
    }
}
