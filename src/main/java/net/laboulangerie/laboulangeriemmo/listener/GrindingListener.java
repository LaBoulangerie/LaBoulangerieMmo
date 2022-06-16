package net.laboulangerie.laboulangeriemmo.listener;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.GrindingCategory;

public class GrindingListener implements Listener {
    public GrindingListener() {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || event.getBlock().hasMetadata("laboulangerie:placed"))
            return;
        giveReward(event.getPlayer(), GrindingCategory.BREAK, event.getBlock().getType().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityKill(EntityDeathEvent event) {
        if (event.isCancelled() || !(event.getEntity().getKiller() instanceof Player))
            return;

        giveReward(event.getEntity().getKiller(), GrindingCategory.KILL, event.getEntity().getType().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.isCancelled() || !(event.getWhoClicked() instanceof Player))
            return;
        Material crafted = event.getRecipe().getResult().getType();

        giveReward(player, GrindingCategory.CRAFT, crafted.toString());

        if (player.getStatistic(Statistic.CRAFT_ITEM, crafted) == 0) {
            giveReward(player, GrindingCategory.FIRST_CRAFT, crafted.toString());
        }
    }

    private void giveReward(Player player, GrindingCategory category, String identifier) {
        if (player.getGameMode() == GameMode.CREATIVE)
            return;
        Set<String> keys = LaBoulangerieMmo.PLUGIN.getConfig().getConfigurationSection("talent-grinding")
                .getKeys(false);

        keys.stream().forEach(talentName -> {
            if (LaBoulangerieMmo.talentsRegistry.getTalent(talentName) == null) return;

            ConfigurationSection section = LaBoulangerieMmo.PLUGIN.getConfig()
                    .getConfigurationSection("talent-grinding." + talentName + "." + category.toString());
            if (section == null)
                return;

            if (section.getKeys(false).contains(identifier))
                LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player).incrementXp(talentName,
                        section.getDouble(identifier));
        });
    }
}
