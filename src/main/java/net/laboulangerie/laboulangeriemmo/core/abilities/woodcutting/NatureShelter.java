package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;

public class NatureShelter {

    private static final int CHECK_INTERVAL_TICKS = 50; // ~2.5 seconds
    private static final int MAX_LEAF_DISTANCE = 4;
    private static final int MIN_CONNECTED_LEAVES = 3;

    private static final int TIER_1_LEVEL = 10;
    private static final int TIER_2_LEVEL = 35;
    private static final int TIER_3_LEVEL = 65;

    public static void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    checkAndHeal(player);
                }
            }
        }.runTaskTimer(LaBoulangerieMmo.PLUGIN, 0L, CHECK_INTERVAL_TICKS);
    }

    private static void checkAndHeal(Player player) {
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);
        if (mmoPlayer == null) return;

        var talent = mmoPlayer.getTalent("lumberjack");
        if (talent == null) return;

        int level = talent.getLevel();
        if (level < TIER_1_LEVEL) return;

        // Check for leaves above
        if (!isUnderValidLeaves(player)) return;

        // Calculate heal amount based on tier
        double healAmount = getHealAmount(level);
        int foodRestore = getFoodRestore(level);

        boolean restoredHealth = false;
        boolean restoredFood = false;

        // Apply if not full health/food
        if (player.getHealth() < player.getMaxHealth()) {
            double newHealth = Math.min(player.getMaxHealth(), player.getHealth() + healAmount);
            player.setHealth(newHealth);
            restoredHealth = true;
        }

        if (foodRestore > 0 && player.getFoodLevel() < 20) {
            player.setFoodLevel(Math.min(20, player.getFoodLevel() + foodRestore));
            restoredFood = true;
        }

        if (restoredHealth || restoredFood) {
            player.getWorld().spawnParticle(Particle.HEART,
                    player.getLocation().add(0, 1.5, 0), 3, 0.3, 0.2, 0.3);
        }
    }

    private static boolean isUnderValidLeaves(Player player) {
        Location loc = player.getLocation();

        for (int y = 1; y <= MAX_LEAF_DISTANCE; y++) {
            Block block = loc.clone().add(0, y, 0).getBlock();
            if (Tag.LEAVES.isTagged(block.getType())) {
                // Check connected leaves
                int connectedLeaves = countConnectedLeaves(block);
                if (connectedLeaves >= MIN_CONNECTED_LEAVES) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int countConnectedLeaves(Block block) {
        int count = 0;
        int[][] offsets = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};

        for (int[] offset : offsets) {
            Block neighbor = block.getRelative(offset[0], offset[1], offset[2]);
            if (Tag.LEAVES.isTagged(neighbor.getType())) {
                count++;
            }
        }
        return count;
    }

    private static double getHealAmount(int level) {
        // Heal per check interval (~2.5s)
        // T3 (65+): ~0.8 HP/s -> 2 HP per 2.5s
        // T2 (35+): 0.6 HP/s -> 1.5 HP per 2.5s
        // T1 (10+): ~0.4 HP/s -> 1 HP per 2.5s (1 heart/5s)
        if (level >= TIER_3_LEVEL) {
            return 2.0;
        } else if (level >= TIER_2_LEVEL) {
            return 1.5;
        } else {
            return 1.0;
        }
    }

    private static int getFoodRestore(int level) {
        // Only restore food at tier 2+
        if (level >= TIER_2_LEVEL) {
            return 1;
        }
        return 0;
    }
}
