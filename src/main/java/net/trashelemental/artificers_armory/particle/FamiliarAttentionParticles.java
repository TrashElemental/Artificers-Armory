package net.trashelemental.artificers_armory.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FamiliarAttentionParticles extends TextureSheetParticle {

    private static final int LIFETIME_TICKS = 140;

    protected FamiliarAttentionParticles(ClientLevel world, double x, double y, double z,
                                         double xd, double yd, double zd) {
        super(world, x, y, z, xd, yd, zd);

        this.lifetime = LIFETIME_TICKS;
        this.gravity = 0f;
        this.quadSize *= 0.2f;

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        this.setAlpha(1.0f);
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age++ >= this.lifetime) this.remove();

        float lifeProgress = (float)this.age / this.lifetime;
        float baseSize = 0.2f * (1.0f - lifeProgress);
        float pulse = 0.85f + 0.15f * Mth.sin(this.age * (random.nextFloat() - 0.5f) * 0.02f);
        this.quadSize = baseSize * pulse;
        this.setAlpha(1.0f - lifeProgress);
        this.oRoll = this.roll;
        this.roll += (random.nextFloat() - 0.5f) * 0.02f;
        this.xd += (random.nextFloat() - 0.5f) * 0.0015;
        this.zd += (random.nextFloat() - 0.5f) * 0.0015;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            FamiliarAttentionParticles particle = new FamiliarAttentionParticles(world, x, y, z, xd, yd, zd);
            particle.pickSprite(spriteSet);
            return particle;
        }
    }

    public static final ParticleRenderType PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.disableDepthTest();
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();
            RenderSystem.enableDepthTest();
        }
    };
}