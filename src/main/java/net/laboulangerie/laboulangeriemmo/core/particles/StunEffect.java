package net.laboulangerie.laboulangeriemmo.core.particles;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class StunEffect extends Effect {

    private LivingEntity livingEntity;

    public StunEffect(Entity entity) {
        super(entity);
        this.livingEntity = (LivingEntity) entity;
    }

    private int i = 0;
    private float radius = 0.5f;
    private int duration = 40; // 2 seconds

    @Override
    public void run() {
        double x = radius * Math.cos(i * 0.2);
        double z = radius * Math.sin(i * 0.2);

        world.spawnParticle(Particle.VILLAGER_ANGRY, location.getX() + x, livingEntity.getEyeLocation().getY() - 0.25,
                location.getZ() + z, 1, 0, 0, 0);

        i++;
        if (i >= duration) cancel();
    }

}
