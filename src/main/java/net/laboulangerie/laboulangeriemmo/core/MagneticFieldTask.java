package net.laboulangerie.laboulangeriemmo.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class MagneticFieldTask extends BukkitRunnable {
    private Location center;
    private int radius;
    private Player player;
    private boolean colorize;
    private List<Material> ores = Arrays.asList(Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.DIAMOND_ORE, Material.REDSTONE_ORE, Material.LAPIS_ORE, Material.EMERALD_ORE, Material.COPPER_ORE,
            Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_REDSTONE_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE_COPPER_ORE, Material.NETHER_GOLD_ORE,
            Material.ANCIENT_DEBRIS, Material.NETHER_QUARTZ_ORE);

    public MagneticFieldTask(Location center, int radius, Player player, boolean colorize) {
        this.center = center;
        this.radius = radius;
        this.player = player;
        this.colorize = colorize;
    }

    @Override
    public void run() {
        List<Block> toUnmark = new ArrayList<Block>();
        for (long x = Math.round(center.getX()) - radius; x < center.getX() + radius; x++) {
            for (long y = Math.round(center.getY()) - radius; y < center.getY() + radius; y++) {
                for (long z = Math.round(center.getZ()) - radius; z < center.getZ() + radius; z++) {
                    Location point = new Location(this.center.getWorld(), x, y, z);
                    if (!isInTheBall(point))
                        continue;

                    Block block = center.getWorld().getBlockAt(point);
                    if (!ores.contains(block.getType()))
                        continue;

                    MarkedBlocksManager.manager().markBlock(block, player);
                    toUnmark.add(block);
                    if (colorize)
                        MarkedBlocksManager.manager().colorize(block, player);
                }
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                toUnmark.stream().forEach(block -> MarkedBlocksManager.manager().unmarkBlock(block, player));
            }
        }.runTaskLater(LaBoulangerieMmo.PLUGIN, 200);
    }

    private boolean isInTheBall(Location point) {
        return center.distanceSquared(point) <= Math.pow(radius, 2);
    }
}
