package com.example.examplemod.client.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BlockDustParticle extends SingleQuadParticle {
    public BlockDustParticle(ClientLevel level, double x, double y, double z,
                             double xa, double ya, double za,
                             float r, float g, float b,
                             SpriteSet sprites) {
        super(level, x, y, z, xa, ya, za, sprites.first());
        this.setParticleSpeed(xa, ya, za);
        this.setColor(r, g, b);
        this.sprites = sprites;
        this.setSpriteFromAge(sprites);
        this.gravity *= 0.5f;
        this.quadSize *= 2f;
    }

    protected final SpriteSet sprites;

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        this.xd *= 0.9;
        this.yd *= 0.9;
        this.zd *= 0.9;
    }

    @Override
    public void extract(QuadParticleRenderState particleTypeRenderState, Camera camera, float partialTickTime) {
        this.alpha = Mth.lerp((age + partialTickTime) / lifetime, 0.75f, 0);
        super.extract(particleTypeRenderState, camera, partialTickTime);
    }

    @Override
    protected @NonNull Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<BlockParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public @Nullable Particle createParticle(BlockParticleOption options, ClientLevel level,
                                                 double x, double y, double z,
                                                 double xa, double ya, double za, RandomSource random) {
            BlockState blockState = options.getState();
            if (!blockState.isAir() && blockState.getRenderShape() == RenderShape.INVISIBLE) {
                return null;
            } else {
                BlockPos pos = BlockPos.containing(x, y, z);
                int tintColor;
                if (blockState.getBlock() instanceof FallingBlock fallingBlock) {
                    tintColor = fallingBlock.getDustColor(blockState, level, pos);
                } else {
                    BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(blockState, 0);
                    if (tintSource != null) {
                        tintColor = tintSource.color(blockState);
                    } else {
                        tintColor = blockState.getMapColor(level, pos).col;
                    }
                }

                float intensity = random.nextIntBetweenInclusive(5, 7) * 0.1f;
                float r = (tintColor >> 16 & 0xFF) / 255.0F * intensity;
                float g = (tintColor >> 8 & 0xFF) / 255.0F * intensity;
                float b = (tintColor & 0xFF) / 255.0F * intensity;
                return new BlockDustParticle(level, x, y, z, xa, ya, za, r, g, b, this.sprite);
            }
        }
    }
}
