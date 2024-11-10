package net.laboulangerie.laboulangeriemmo.core.particles;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;

public class TrailEffect extends Effect {
    private int i = 0;

    public TrailEffect(Entity entity) {
        super(entity);
        period = 5;
    }

    @Override
    public void run() {
        world.spawnParticle(Particle.FIREWORK, entity.getLocation(), 0, 0, 0, 0, 8);
        i++;
        if (i == 4) cancel();
    }
}
