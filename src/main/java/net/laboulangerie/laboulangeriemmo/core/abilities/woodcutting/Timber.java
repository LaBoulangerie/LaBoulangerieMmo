package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
    private static final int[][] REL_COORDINATES =
            {{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 0, 1}, {1, 0, -1}, {-1, 0, 1},
                    {-1, 0, -1}, {0, 1, 0}, {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1}, {1, 1, 1},
                    {1, 1, -1}, {-1, 1, 1}, {-1, 1, -1}, {1, -1, 0}, {-1, -1, 0}, {0, -1, 1},
                    {0, -1, -1}, {1, -1, 1}, {1, -1, -1}, {-1, -1, 1}, {-1, -1, -1}};

    private static final int TIER_ONE_RANGE = 3;
    private static final int TIER_TWO_RANGE = 5;
    private static final int TIER_THREE_RANGE = 7;

    private Material initType;
    private Location initLocation;
    private Block block;
    private Player player;

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        if (!event.getKeyStreak().match(new KeyStreak(ComboKey.LEFT, ComboKey.LEFT, ComboKey.RIGHT)))
            return false; // We do this check first to avoid ray casting for nothing

        player = event.getPlayer();
        block = player.getTargetBlockExact(4);

        return block != null && Tag.LOGS.isTagged(block.getType())
            && LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) == null
            && LaBoulangerieMmo.PLUGIN.getTalentProtection().canBreak(player, block);
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        if (!LaBoulangerieMmo.PLUGIN.getTalentProtection().canBreak(player, block)) return;

        initType = block.getType();
        initLocation = block.getLocation();
        int range = rangeForLevel(level);
        List<List<Block>> treeByDepth = findTreeByDepth(range);

        for (int depth = 0; depth < treeByDepth.size(); depth++) {
            List<Block> allowedBlocks = treeByDepth.get(depth).stream()
                    .filter(candidate -> LaBoulangerieMmo.PLUGIN.getTalentProtection()
                            .canBreak(player, candidate))
                    .toList();
            if (allowedBlocks.isEmpty()) continue;

            long delay = depth * 5L;
            if (delay == 0L) {
                allowedBlocks.forEach(candidate -> breakIfAllowed(candidate, range));
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        allowedBlocks.forEach(candidate -> Timber.this.breakIfAllowed(candidate, range));
                    }
                }.runTaskLater(LaBoulangerieMmo.PLUGIN, delay);
            }
        }
    }

    int rangeForLevel(int level) {
        if (level >= getTier(2)) return TIER_THREE_RANGE;
        if (level >= getTier(1)) return TIER_TWO_RANGE;
        return TIER_ONE_RANGE;
    }

    private List<List<Block>> findTreeByDepth(int range) {
        List<List<Block>> treeByDepth = new ArrayList<>();
        Queue<TreeNode> queue = new ArrayDeque<>();
        Set<Location> visited = new HashSet<>();
        queue.add(new TreeNode(block, 0));

        while (!queue.isEmpty()) {
            TreeNode node = queue.remove();
            Location location = node.block().getLocation();
            if (!visited.add(location) || !isMatchingTreeBlock(node.block(), range)) continue;

            while (treeByDepth.size() <= node.depth()) treeByDepth.add(new ArrayList<>());
            treeByDepth.get(node.depth()).add(node.block());

            for (int[] coordinate : REL_COORDINATES) {
                Block neighbour = node.block().getRelative(coordinate[0], coordinate[1], coordinate[2]);
                if (!visited.contains(neighbour.getLocation()) && isInsideRange(neighbour, range)) {
                    queue.add(new TreeNode(neighbour, node.depth() + 1));
                }
            }
        }

        return treeByDepth;
    }

    private boolean isMatchingTreeBlock(Block candidate, int range) {
        Material logType = Material.getMaterial(initType.toString().replace("_WOOD", "_LOG"));
        Material woodType = Material.getMaterial(initType.toString().replace("_LOG", "_WOOD"));
        return isInsideRange(candidate, range)
                && (candidate.getType() == logType || candidate.getType() == woodType);
    }

    private boolean isInsideRange(Block candidate, int range) {
        return candidate.getY() >= initLocation.getBlockY()
                && Math.abs(candidate.getX() - initLocation.getBlockX()) <= range
                && Math.abs(candidate.getZ() - initLocation.getBlockZ()) <= range;
    }

    private void breakIfAllowed(Block candidate, int range) {
        if (!isMatchingTreeBlock(candidate, range)
                || !LaBoulangerieMmo.PLUGIN.getTalentProtection().canBreak(player, candidate)) {
            return;
        }

        GrindingListener.giveReward(player, GrindingCategory.BREAK, candidate.getType().toString(), false);
        candidate.breakNaturally(null, true);
    }

    private record TreeNode(Block block, int depth) {}
}
