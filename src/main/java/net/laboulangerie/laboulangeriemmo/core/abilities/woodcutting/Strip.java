package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class Strip extends AbilityExecutor {
    public Strip(AbilityArchetype archetype) {
        super(archetype);
    }

    // Relative coordinates of every neighbours that we want to check
    private static int[][] relCoordinates = {{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 0, 1}, {1, 0, -1},
            {-1, 0, 1}, {-1, 0, -1}, {0, 1, 0}, {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1}, {1, 1, 1}, {1, 1, -1},
            {-1, 1, 1}, {-1, 1, -1}, {1, -1, 0}, {-1, -1, 0}, {0, -1, 1}, {0, -1, -1}, {1, -1, 1}, {1, -1, -1},
            {-1, -1, 1}, {-1, -1, -1}};

    private int range = 5;

    private Material initType;
    private Location initLocation;

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Block block = event.getClickedBlock();

        return event.getPlayer().isSneaking() && event.getItem() != null
                && event.getItem().getType().toString().endsWith("_AXE") && Tag.LOGS.isTagged(block.getType())
                && !block.getType().toString().startsWith("STRIPPED") && !(block.hasMetadata("laboulangerie:placed"));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Block initBlock = event.getPlayer().getTargetBlockExact(5);
        initType = initBlock.getType();
        initLocation = initBlock.getLocation();
        initBlock.setType(Material.getMaterial("STRIPPED_" + initBlock.getType().toString()));
        stripNeighbours(initBlock, event.getPlayer());
    }

    private void stripNeighbours(Block block, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = block.getLocation();
                for (int[] coordinate : Strip.relCoordinates) {
                    Location neighbourLoc = loc.clone().add(coordinate[0], coordinate[1], coordinate[2]);
                    Block neighbour = neighbourLoc.getBlock();

                    if ((neighbour.getType() == Material.getMaterial(initType.toString().replace("_WOOD", "_LOG"))
                            || neighbour.getType() == Material
                                    .getMaterial(initType.toString().replace("_LOG", "_WOOD")))
                            && neighbour.getY() >= initLocation.getBlockY()
                            && Math.abs(neighbour.getX() - initLocation.getBlockX()) <= range
                            && Math.abs(neighbour.getZ() - initLocation.getBlockZ()) <= range) {
                        // Change to stripped variant and preserve rotation, getAsString() returns
                        // something like: minecraft:oak_log[axis=y]
                        String direction =
                                "minecraft:stripped_" + neighbour.getBlockData().getAsString().split("minecraft:")[1];
                        neighbour.setBlockData(Bukkit.getServer().createBlockData(direction));

                        // Break neighbours of neighbour recursively
                        stripNeighbours(neighbour, player);
                    }
                }
            }
        }.runTaskLater(LaBoulangerieMmo.PLUGIN, 5);
    }
}
