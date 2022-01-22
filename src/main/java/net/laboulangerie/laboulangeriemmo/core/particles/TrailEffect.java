package net.laboulangerie.laboulangeriemmo.core.particles;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class TrailEffect extends Effect {
    private int i = 0;

    public TrailEffect(Player player) {
        super(player);
        period = 5;
    }

    @Override
    public void run() {
        world.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 0, 0, 0, 0, 8);
        i++;
        if (i == 4)
            cancel();
    }
}
