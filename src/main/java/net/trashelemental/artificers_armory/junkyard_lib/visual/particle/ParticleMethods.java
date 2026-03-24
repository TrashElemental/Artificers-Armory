package net.trashelemental.artificers_armory.junkyard_lib.visual.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.concurrent.ThreadLocalRandom;

public class ParticleMethods {

    /**
     * Particles burst outwards from an entity.
     */
    public static void ParticlesBurst(Level level, ParticleOptions particleType, double x, double y, double z, int count, double speed) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particleType, x, y, z, count, 0, 0, 0, speed);
        }
    }

    /**
     * Particles appear around an entity.
     */
    public static void ParticlesAround(Level level, ParticleOptions particleType, Entity entity, int count, double spread) {
        if (level.isClientSide && entity != null) {
            for (int i = 0; i < count; i++) {
                double offsetX = (ThreadLocalRandom.current().nextDouble() - 0.5) * spread;
                double offsetY = (ThreadLocalRandom.current().nextDouble() - 0.5) * spread;
                double offsetZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * spread;

                level.addParticle(particleType, entity.getX() + offsetX, entity.getEyeY() + offsetY, entity.getZ() + offsetZ,
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     *  Server-side version of the above
     */
    public static void ParticlesAroundServerSide(Level level, ParticleOptions particleType, double x, double y, double z, int count, double spread) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particleType, x, y, z, count,
                    (ThreadLocalRandom.current().nextDouble() - 0.5) * spread,
                    (ThreadLocalRandom.current().nextDouble() - 0.5) * spread,
                    (ThreadLocalRandom.current().nextDouble() - 0.5) * spread,
                    0.0D);
        }
    }

    /**
     * Creates a trail of particles from one entity to another.
     */
    public static void ParticleTrailEntityToEntity(Level level, ParticleOptions particleType, Entity source, Entity destination, int count) {
        if (level instanceof ServerLevel serverLevel) {

            double deltaX = destination.getX() - source.getX();
            double deltaY = destination.getY() - source.getY();
            double deltaZ = destination.getZ() - source.getZ();

            for (int i = 0; i <= count; i++) {
                double progress = i / (double) count;
                double particleX = source.getX() + deltaX * progress;
                double particleY = source.getY() + deltaY * progress + 0.5;
                double particleZ = source.getZ() + deltaZ * progress;

                serverLevel.sendParticles(particleType, particleX, particleY, particleZ,
                        1, 0, 0, 0, 0);
            }
        }
    }

    /**
     * Creates a trail of particles from a block to an entity.
     */
    public static void ParticleTrailBlockToEntity(Level level, ParticleOptions particleType, BlockPos block, Entity entity, int count) {
        if (level instanceof ServerLevel serverLevel) {

            double deltaX = entity.getX() - block.getX();
            double deltaY = entity.getEyeY() - block.getY();
            double deltaZ = entity.getZ() - block.getZ();

            for (int i = 0; i <= count; i++) {
                double progress = i / (double) count;
                double particleX = block.getX() + 0.5 + deltaX * progress;
                double particleY = block.getY() + 0.5 + deltaY * progress;
                double particleZ = block.getZ() + 0.5 + deltaZ * progress;

                serverLevel.sendParticles(particleType, particleX, particleY, particleZ,
                        1, 0, 0, 0, 0);
            }
        }
    }

    /**
     * Creates a trail of particles from one block to another.
     */
    public static void ParticleTrailBlockToBlock(Level level, ParticleOptions particleType, BlockPos source, BlockPos destination, int count) {
        if (level instanceof ServerLevel serverLevel) {

            double deltaX = destination.getX() - source.getX();
            double deltaY = destination.getY() - source.getY();
            double deltaZ = destination.getZ() - source.getZ();

            for (int i = 0; i <= count; i++) {
                double progress = i / (double) count;
                double particleX = source.getX() + 0.5 + deltaX * progress;
                double particleY = source.getY() + 0.5 + deltaY * progress;
                double particleZ = source.getZ() + 0.5 + deltaZ * progress;

                serverLevel.sendParticles(particleType, particleX, particleY, particleZ,
                        1, 0, 0, 0, 0);
            }
        }
    }


    public static void ParticlesInBox(ServerLevel level, AABB box, ParticleOptions particle, int count) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < count; i++) {
            double x = random.nextDouble(box.minX, box.maxX);
            double y = random.nextDouble(box.minY, box.maxY);
            double z = random.nextDouble(box.minZ, box.maxZ);
            level.sendParticles(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }

}

