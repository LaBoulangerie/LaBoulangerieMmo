package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.api.player.GrindingCategory;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;
import net.laboulangerie.laboulangeriemmo.listener.GrindingListener;

public class Timber extends AbilityExecutor {
    public Timber(AbilityArchetype archetype) {
        super(archetype);
    }

    // Relative coordinates of every neighbours that we want to check
    private static int[][] relCoordinates = { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 }, { 1, 0, 1 },
            { 1, 0, -1 }, { -1, 0, 1 },
            { -1, 0, -1 }, { 0, 1, 0 }, { 1, 1, 0 }, { -1, 1, 0 }, { 0, 1, 1 }, { 0, 1, -1 }, { 1, 1, 1 },
            { 1, 1, -1 }, { -1, 1, 1 }, { -1, 1, -1 }, { 1, -1, 0 }, { -1, -1, 0 }, { 0, -1, 1 },
            { 0, -1, -1 }, { 1, -1, 1 }, { 1, -1, -1 }, { -1, -1, 1 }, { -1, -1, -1 } };

    private int range = 5;

    private Material initType;
    private Location initLocation;
    private Block block;
    private Player player;

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        if (!event.getKeyStreak().match(new KeyStreak(ComboKey.LEFT, ComboKey.LEFT, ComboKey.LEFT)))
            return false; // We do this check first to avoid ray casting for nothing

        player = event.getPlayer();
        block = player.getTargetBlockExact(4);

        return block != null && Tag.LOGS.isTagged(block.getType())
                && LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) == null;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        initType = block.getType();
        initLocation = block.getLocation();
        GrindingListener.giveReward(((ComboCompletedEvent) baseEvent).getPlayer(), GrindingCategory.BREAK,
                block.getType().toString(), false);
        block.breakNaturally();
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

                    if ((neighbour.getType() == Material.getMaterial(initType.toString().replace("_WOOD", "_LOG"))
                            || neighbour.getType() == Material
                                    .getMaterial(initType.toString().replace("_LOG", "_WOOD")))
                            && neighbour.getY() >= initLocation.getBlockY()
                            && Math.abs(neighbour.getX() - initLocation.getBlockX()) <= range
                            && Math.abs(neighbour.getZ() - initLocation.getBlockZ()) <= range) {
                        // Check if it's in blockus registry
                        if (LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(neighbour) != null) {
                            continue;
                        }

                        // Try to break the block
                        BlockBreakEvent breakEvent = new BlockBreakEvent(neighbour, player);
                        LaBoulangerieMmo.PLUGIN.getServer().getPluginManager().callEvent(breakEvent);
                        if (breakEvent.isCancelled()) {
                            continue;
                        }

                        // Give xp for breaking the block
                        GrindingListener.giveReward(player, GrindingCategory.BREAK, neighbour.getType().toString(),
                                false);
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
