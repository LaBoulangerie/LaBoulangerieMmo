package fr.laboulangerie.laboulangeriemmo.core;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;



public class ParticleEffect {
    public void createHelix(Player player) {
        Location loc = player.getLocation();
        World world = player.getWorld();
        int radius = 2;
        for(double y = 0; y <= 50; y+=0.05) {
            double x = radius * Math.cos(y);
            double z = radius * Math.sin(y);
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 3);
            world.spawnParticle(Particle.REDSTONE, loc.getX() + x, loc.getY() + y, loc.getZ() + z, 3, 1, 0.6, 0, dustOptions);
        }
	}
}
