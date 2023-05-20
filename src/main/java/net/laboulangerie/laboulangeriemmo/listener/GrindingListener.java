package net.laboulangerie.laboulangeriemmo.listener;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.CraftItemEvent;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.GrindingCategory;
import net.laboulangerie.laboulangeriemmo.utils.MythicMobsSupport;

public class GrindingListener implements Listener {
    private static FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();

    public GrindingListener() {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.hasMetadata("laboulangerie:placed"))
            return;

        if (block.getState().getBlockData() instanceof Ageable) {
            Ageable ageable = ((Ageable) block.getState().getBlockData());
            if (ageable.getAge() != ageable.getMaximumAge() &&
                    !LaBoulangerieMmo.PLUGIN.getConfig().getStringList("ageable-ignored-blocks")
                            .contains(block.getType().toString()))
                return;
        }
        giveReward(event.getPlayer(), GrindingCategory.BREAK, block.getType().toString(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityKill(EntityDeathEvent event) {
        if (event.isCancelled() || !(event.getEntity().getKiller() instanceof Player))
            return;

        boolean isMythicMob = false;
        if (LaBoulangerieMmo.MYTHICMOBS_SUPPORT) {
            try {
                isMythicMob = MythicMobsSupport.tryToGiveMythicReward(event.getEntity(), event.getEntity().getKiller());
            } catch (Exception e) {
            }
        }

        if (!isMythicMob)
            giveReward(event.getEntity().getKiller(), GrindingCategory.KILL, event.getEntity().getType().toString(),
                    event.getEntity().getEntitySpawnReason() == SpawnReason.SPAWNER);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.isCancelled() || !(event.getWhoClicked() instanceof Player))
            return;
        Material crafted = event.getRecipe().getResult().getType();

        giveReward(player, GrindingCategory.CRAFT, crafted.toString(), false);

        if (player.getStatistic(Statistic.CRAFT_ITEM, crafted) == 0) {
            giveReward(player, GrindingCategory.FIRST_CRAFT, crafted.toString(), false);
        }
    }

    public static void giveReward(Player player, GrindingCategory category, String identifier, boolean isSpawnerMob) {
        if (player.getGameMode() == GameMode.CREATIVE)
            return;
        Set<String> keys = LaBoulangerieMmo.PLUGIN.getConfig().getConfigurationSection("talent-grinding")
                .getKeys(false);

        keys.stream().forEach(talentName -> {
            if (LaBoulangerieMmo.talentsRegistry.getTalent(talentName) == null)
                return;

            ConfigurationSection section = LaBoulangerieMmo.PLUGIN.getConfig()
                    .getConfigurationSection("talent-grinding." + talentName + "." + category.toString());
            if (section == null)
                return;

            if (section.getKeys(false).contains(identifier)) {
                double xpAmount = section.getDouble(identifier);
                if (isSpawnerMob && category == GrindingCategory.KILL) {
                    if (config.contains("decrease-spawner-mobs-by." + identifier)) {
                        xpAmount -= config.getDouble("decrease-spawner-mobs-by." + identifier);
                    } else if (config.contains("decrease-spawner-mobs-by.*")) {
                        xpAmount -= config.getDouble("decrease-spawner-mobs-by.*");
                    }
                    xpAmount = xpAmount < 0 ? 0 : xpAmount;
                }
                LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player).incrementXp(talentName, xpAmount);
            }
        });
    }
}
