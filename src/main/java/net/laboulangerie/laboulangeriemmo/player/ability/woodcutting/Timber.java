package net.laboulangerie.laboulangeriemmo.player.ability.woodcutting;

import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class Timber extends AbilityExecutor {
    // Relative coordinates of every neighbours that we want to check
    private static int[][] relCoordinates = {
            { 1, 0, 0 },
            { -1, 0, 0 },
            { 0, 0, 1 },
            { 0, 0, -1 },
            { 1, 0, 1 },
            { 1, 0, -1 },
            { -1, 0, 1 },
            { -1, 0, -1 },
            { 0, 1, 0 },
            { 1, 1, 0 },
            { -1, 1, 0 },
            { 0, 1, 1 },
            { 0, 1, -1 },
            { 1, 1, 1 },
            { 1, 1, -1 },
            { -1, 1, 1 },
            { -1, 1, -1 },
            { 1, -1, 0 },
            { -1, -1, 0 },
            { 0, -1, 1 },
            { 0, -1, -1 },
            { 1, -1, 1 },
            { 1, -1, -1 },
            { -1, -1, 1 },
            { -1, -1, -1 }
    };

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.BREAK;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block block = event.getBlock();

        return block != null && Tag.LOGS.isTagged(block.getType()) && !(block.hasMetadata("laboulangerie:placed"));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block block = event.getBlock();
        breakNeighbours(block);
    }

    private void breakNeighbours(Block block) {
        new BukkitRunnable() {
            @Override
            public void run() {

                Location loc = block.getLocation();
                for (int[] coordinate : Timber.relCoordinates) {
                    Location neighbourLoc = loc.clone().add(coordinate[0], coordinate[1], coordinate[2]);
                    Block neighbour = neighbourLoc.getBlock();

                    if (Tag.LOGS.isTagged(neighbour.getType())) {
                        // Drop the item and spawn block particles
                        neighbour.breakNaturally(null, true);
                        // Break neighbours of neighbour recursively
                        breakNeighbours(neighbour);
                    }
                }
                ;
            }
        }.runTaskLater(LaBoulangerieMmo.PLUGIN, 5);
    }
}