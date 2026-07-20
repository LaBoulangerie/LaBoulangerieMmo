package net.laboulangerie.laboulangeriemmo.core.abilities.mining;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import net.laboulangerie.laboulangeriemmo.core.abilities.mining.LuckyVein.FortuneBonus;

public class LuckyVeinListener implements Listener {

    private static final Random random = new Random();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        FortuneBonus bonus = LuckyVein.getActiveBonus(player.getUniqueId());

        if (bonus == null) return;

        Block block = event.getBlock();
        Material type = block.getType();

        // Only apply to ores
        if (!isOre(type)) return;

        // Get current drops and add bonus drops based on fortune bonus
        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());

        for (ItemStack drop : drops) {
            // Calculate bonus drops: fortune bonus as a chance multiplier
            // bonus of 0.5 = 50% chance for +1 drop
            // bonus of 1.0 = 100% chance for +1 drop
            if (random.nextFloat() < bonus.bonus) {
                ItemStack bonusDrop = drop.clone();
                bonusDrop.setAmount(1);
                block.getWorld().dropItemNaturally(block.getLocation(), bonusDrop);
            }
        }
    }

    private boolean isOre(Material type) {
        // Check if material is an ore using Tag if available, or manually
        if (Tag.COAL_ORES.isTagged(type)) return true;
        if (Tag.COPPER_ORES.isTagged(type)) return true;
        if (Tag.IRON_ORES.isTagged(type)) return true;
        if (Tag.GOLD_ORES.isTagged(type)) return true;
        if (Tag.REDSTONE_ORES.isTagged(type)) return true;
        if (Tag.LAPIS_ORES.isTagged(type)) return true;
        if (Tag.DIAMOND_ORES.isTagged(type)) return true;
        if (Tag.EMERALD_ORES.isTagged(type)) return true;

        // Nether ores
        return type == Material.NETHER_QUARTZ_ORE || type == Material.NETHER_GOLD_ORE || type == Material.ANCIENT_DEBRIS;
    }
}
