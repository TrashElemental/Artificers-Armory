package net.trashelemental.artificers_armory.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PotionCloudParticles extends TextureSheetParticle {

    protected PotionCloudParticles(ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
        super(world, x, y, z, xd, yd, zd);

        this.gravity = 0F;
        this.lifetime = 60;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.quadSize *= 4;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age++ >= this.lifetime) this.remove();

        float lifeProgress = (float)this.age / this.lifetime;
        this.alpha = 0.6F * (1.0F - lifeProgress);
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

        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double r, double g, double b) {
            PotionCloudParticles particle = new PotionCloudParticles(world, x, y, z, 0, 0, 0);

            float variation = 0.9f + world.random.nextFloat() * 0.2f;
            float avg = (float)((r + g + b) / 3.0);
            float boost = 1.7f;
            float nr = avg + (float)(r - avg) * boost;
            float ng = avg + (float)(g - avg) * boost;
            float nb = avg + (float)(b - avg) * boost;

            nr *= variation;
            ng *= variation;
            nb *= variation;
            particle.setColor(Mth.clamp(nr, 0f, 1f), Mth.clamp(ng, 0f, 1f), Mth.clamp(nb, 0f, 1f));

            particle.pickSprite(spriteSet);
            return particle;
        }
    }
}
