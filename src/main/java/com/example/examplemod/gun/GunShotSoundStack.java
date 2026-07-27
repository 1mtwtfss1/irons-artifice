package com.example.examplemod.gun;

import com.example.examplemod.client.GunShotSoundSettings;
import com.example.examplemod.data.PlayableSound;
import com.example.examplemod.network.ClientboundGunshotSoundPacket;
import com.example.examplemod.registry.SoundRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class GunShotSoundStack {
    GunShotSoundSettings baseSound;
    GunShotSoundSettings echoSound;
    PlayableSound dryFireSound;
    private final List<PlayableSound> accents = new ArrayList<>();

    public GunShotSoundStack(GunShotSoundSettings baseSound, GunShotSoundSettings echoSound, PlayableSound dryFireSound) {
        this.baseSound = baseSound;
        this.dryFireSound = dryFireSound;
        this.echoSound = echoSound;
    }

    public GunShotSoundStack(GunShotSoundSettings baseSound, PlayableSound dryFireSound) {
        this(baseSound, new GunShotSoundSettings(SoundRegistry.GENERIC_BULLET_ECHO, 0.7f, 0.9f, 64f, 128f, 192f), dryFireSound);
    }

    public void addAccent(PlayableSound options) {
        this.accents.add(options);
    }

    public void setBaseSound(GunShotSoundSettings sound) {
        this.baseSound = sound;
    }

    public List<PlayableSound> getAccentSounds() {
        return List.copyOf(accents);
    }

    public void playGunShotSound(Level level, Vec3 pos) {
        for (PlayableSound sound : accents) {
            sound.play(level, pos, SoundSource.NEUTRAL);
        }
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, null, pos.x, pos.y, pos.z, 12 * 16,
                    new ClientboundGunshotSoundPacket(SoundSource.NEUTRAL, pos.x, pos.y, pos.z, List.of(this.baseSound, this.echoSound)));
        }
    }

    public void playDryFireSound(Level level, Vec3 pos) {
        this.dryFireSound.play(level, pos, SoundSource.NEUTRAL);
    }
}
