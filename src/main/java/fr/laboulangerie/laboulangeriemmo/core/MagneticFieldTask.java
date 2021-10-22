package fr.laboulangerie.laboulangeriemmo.core;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MagneticFieldTask extends BukkitRunnable {
    private Location center;
    private int radius;
    private Player player;
    private List<Material> ores = Arrays.asList(Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE);

    public MagneticFieldTask(Location center, int radius, Player player) {
        this.center = center;
        this.radius = radius;
        this.player = player;
    }

    @Override
    public void run() {
        for(long x = Math.round(center.getX()) - radius; x < center.getX() + radius; x++) {
            for(long y = Math.round(center.getY()) - radius; y < center.getY() + radius; y++) {
                for(long z = Math.round(center.getZ()) - radius; z < center.getZ() + radius; z++) {
                    Location point = new Location(this.center.getWorld(), x, y, z);
                    if (!isInTheBall(point)) continue;

                    Block block = center.getWorld().getBlockAt(point);
                    if (!ores.contains(block.getType())) continue;

                    MarkedBlocksManager.manager().markBlock(block, player);
                }
            }
        }
    }
    private boolean isInTheBall(Location point) {
        return center.distanceSquared(point) <= Math.pow(radius, 2);
    }
}
