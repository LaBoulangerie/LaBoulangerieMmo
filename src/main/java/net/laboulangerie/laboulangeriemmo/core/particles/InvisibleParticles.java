package net.laboulangerie.laboulangeriemmo.core.particles;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.hiding.InvisiblePlayer;

public class InvisibleParticles extends Effect {

    public InvisibleParticles(Entity entity) {
        super(entity);
        period = 5;
    }

    @Override
    public void run() {
        InvisiblePlayer invisiblePlayer = InvisiblePlayer.getInvisiblePlayer((Player) entity);
        if (invisiblePlayer == null) cancel();

        final Location printLocation = entity.getLocation();
        final Block block = printLocation.clone().add(0, -1, 0).getBlock();
        if (block.getType().equals(Material.AIR)) return;

        printLocation.getWorld().spawnParticle(Particle.TOWN_AURA, new Location(printLocation.getWorld(), printLocation.getX(), printLocation.getY(),printLocation.getZ()), 5);
    }
    
}
