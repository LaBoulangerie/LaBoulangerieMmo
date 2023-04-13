package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class Timber extends AbilityExecutor {
    public Timber(AbilityArchetype archetype) {
        super(archetype);
    }

    // Relative coordinates of every neighbours that we want to check
    private static int[][] relCoordinates =
            {{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 0, 1}, {1, 0, -1}, {-1, 0, 1},
                    {-1, 0, -1}, {0, 1, 0}, {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1}, {1, 1, 1},
                    {1, 1, -1}, {-1, 1, 1}, {-1, 1, -1}, {1, -1, 0}, {-1, -1, 0}, {0, -1, 1},
                    {0, -1, -1}, {1, -1, 1}, {1, -1, -1}, {-1, -1, 1}, {-1, -1, -1}};

    private int range = 5;

    private Material initType;
    private Location initLocation;

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block block = event.getBlock();

        return block != null && Tag.LOGS.isTagged(block.getType())
                && !(block.hasMetadata("laboulangerie:placed"));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block initBlock = event.getBlock();
        initType = initBlock.getType();
        initLocation = initBlock.getLocation();
        breakNeighbours(initBlock);
    }

    private void breakNeighbours(Block block) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = block.getLocation();
                for (int[] coordinate : Timber.relCoordinates) {
                    Location neighbourLoc =
                            loc.clone().add(coordinate[0], coordinate[1], coordinate[2]);
                    Block neighbour = neighbourLoc.getBlock();

                    if ((neighbour.getType() == Material
                            .getMaterial(initType.toString().replace("_WOOD", "_LOG"))
                            || neighbour.getType() == Material
                                    .getMaterial(initType.toString().replace("_LOG", "_WOOD")))
                            && neighbour.getY() >= initLocation.getBlockY()
                            && Math.abs(neighbour.getX() - initLocation.getBlockX()) <= range
                            && Math.abs(neighbour.getZ() - initLocation.getBlockZ()) <= range) {
                        // Drop the item and spawn block particles
                        neighbour.breakNaturally(null, true);
                        // Break neighbours of neighbour recursively
                        breakNeighbours(neighbour);
                    }
                }
            }
        }.runTaskLater(LaBoulangerieMmo.PLUGIN, 5);
    }
}
