package net.laboulangerie.laboulangeriemmo.core.thehunter.hiding;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class InvisibleParticles {

    public static void renderParticles(Player player) {
        final InvisiblePlayer invisiblePlayer = InvisiblePlayer.getInvisiblePlayer(player);
        if (invisiblePlayer == null) return;

        if (invisiblePlayer.getAbilityLevel() < 3) {
            final Location printLocation = invisiblePlayer.getPlayer().getLocation();
            final Block block = printLocation.clone().add(0, -1, 0).getBlock();
            if (block.getType().equals(Material.AIR)) return;

            printLocation.getWorld().spawnParticle(Particle.TOWN_AURA, new Location(printLocation.getWorld(), printLocation.getX(), block.getY() + 1,printLocation.getZ()),5);
        }
    }
}
