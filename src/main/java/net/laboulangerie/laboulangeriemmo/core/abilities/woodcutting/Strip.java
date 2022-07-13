package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;

public class Strip extends AbilityExecutor {
    public Strip(AbilityArchetype archetype) {
        super(archetype);
    }

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

    private int range = 5;

    private Material initType;
    private Location initLocation;

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        Block block = event.getPlayer().getTargetBlock(5);

        return new KeyStreak(ComboKey.LEFT, ComboKey.LEFT, ComboKey.LEFT).match(event.getKeyStreak()) && block != null && Tag.LOGS.isTagged(block.getType()) && !(block.hasMetadata("laboulangerie:placed"));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        Block initBlock = event.getPlayer().getTargetBlock(5);
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

                    if ((neighbour.getType() == Material.getMaterial(initType.toString().replace("_WOOD", "_LOG")) || neighbour.getType() == Material.getMaterial(initType.toString().replace("_LOG", "_WOOD")))
                            && neighbour.getY() >= initLocation.getBlockY()
                            && Math.abs(neighbour.getX() - initLocation.getBlockX()) <= range
                            && Math.abs(neighbour.getZ() - initLocation.getBlockZ()) <= range
                    ) {
                        neighbour.setType(Material.getMaterial("STRIPPED_" + neighbour.getType().toString()));
                        // Break neighbours of neighbour recursively
                        stripNeighbours(neighbour , player);
                    }
                }
            }
        }.runTaskLater(LaBoulangerieMmo.PLUGIN, 5);
    }
}