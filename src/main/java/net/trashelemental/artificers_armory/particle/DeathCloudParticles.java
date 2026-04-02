package net.trashelemental.artificers_armory.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DeathCloudParticles extends TextureSheetParticle {
    private final float initialQuadSize;

    protected DeathCloudParticles(ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
        super(world, x, y, z, xd, yd, zd);

        this.gravity = 0.01F;
        this.lifetime = 100;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.quadSize *= 3f;
        this.initialQuadSize = this.quadSize;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age++ >= this.lifetime) this.remove();

        float lifeProgress = (float)this.age / this.lifetime;
        float baseSize = 0.3f * (1.0f - lifeProgress);
        float pulse = 0.85f + 0.15f * Mth.sin(this.age * (random.nextFloat() - 0.5f) * 0.2f);
        this.quadSize = baseSize * pulse;
        this.alpha = 0.9F * (1.0F - lifeProgress);
        this.oRoll = this.roll;
        this.roll += (random.nextFloat() - 0.5f) * 0.1f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
            DeathCloudParticles particle = new DeathCloudParticles(world, x, y, z, xd, yd, zd);
            particle.pickSprite(spriteSet);
            return particle;
        }
    }
}
