package com.example.examplemod.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MagazineContents(int count) {
    public static final MagazineContents EMPTY = new MagazineContents(0);

    public static final Codec<MagazineContents> CODEC =
            Codec.INT.xmap(MagazineContents::new, MagazineContents::count);

    public static final StreamCodec<ByteBuf, MagazineContents> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(MagazineContents::new, MagazineContents::count);

    public boolean isEmpty() {
        return this.count <= 0;
    }

    public int missing(int capacity) {
        return Math.max(0, capacity - this.count);
    }

    public boolean isFull(int capacity) {
        return this.count >= capacity;
    }

    public MagazineContents with(int newCount) {
        return new MagazineContents(Math.max(0, newCount));
    }

    /** Returns a copy with one round removed (floored at zero). */
    public MagazineContents deplete() {
        return with(this.count - 1);
    }
}
