package com.example.examplemod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class GunShotEchoSoundInstance extends AbstractSoundInstance {
    private static final int PEAK_RANGE_CHUNKS = 6;
    private static final int FALLOFF_RANGE_CHUNKS = 4;

    public GunShotEchoSoundInstance(SoundEvent event, SoundSource source, RandomSource random, Vec3 position, float minPitch, float maxPitch) {
        super(event, source, random);
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
        double distance = Minecraft.getInstance().player.position().subtract(position).length();
        this.attenuation = Attenuation.NONE;
//        this.volume = 1f;
        this.volume = (float) Math.max(0f, 1f - Math.abs(distance - (PEAK_RANGE_CHUNKS * 16)) / (FALLOFF_RANGE_CHUNKS * 16));
        this.pitch = Mth.lerp(random.nextFloat(), minPitch, maxPitch);
        this.relative = false;
    }
}
