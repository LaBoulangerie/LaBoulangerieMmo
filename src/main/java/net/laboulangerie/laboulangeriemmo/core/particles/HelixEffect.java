package net.laboulangerie.laboulangeriemmo.core.particles;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;

public class HelixEffect extends Effect {

    public HelixEffect(Entity entity) {
        super(entity);
    }

    private int radius = 2;
    private int i = 0;
    private double y = 0;

    @Override
    public void run() {
        y += 0.05;
        double x = radius * Math.cos(i * 0.1);
        double z = radius * Math.sin(i * 0.1);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 2);
        world.spawnParticle(Particle.REDSTONE, location.getX() + x, location.getY() + y,
                location.getZ() + z, 3, 1, 0.6, 0, dustOptions);
        i++;
        if (y >= 3) cancel();
    }
}
