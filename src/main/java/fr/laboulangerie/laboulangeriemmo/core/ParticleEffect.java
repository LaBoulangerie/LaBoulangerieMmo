package fr.laboulangerie.laboulangeriemmo.core;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;


public class ParticleEffect {
    public void createHelix(Player player) {
        new BukkitRunnable() {
            Location loc = player.getLocation();
            World world = player.getWorld();
            int radius = 2;
            int i = 0;
            double y = 0;
            @Override
            public void run() {
                y += 0.05;
                double x = radius * Math.cos(i * 0.1);
                double z = radius * Math.sin(i * 0.1);
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 2);
                world.spawnParticle(Particle.REDSTONE, loc.getX() + x, loc.getY() + y, loc.getZ() + z, 3, 1, 0.6, 0, dustOptions);
                i++;
                if (y >= 3) this.cancel();
            }
        }.runTaskTimerAsynchronously(LaBoulangerieMmo.PLUGIN, 0, 1);
    }
}
