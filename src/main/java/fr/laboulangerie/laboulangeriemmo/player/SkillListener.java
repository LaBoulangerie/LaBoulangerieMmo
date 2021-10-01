package fr.laboulangerie.laboulangeriemmo.player;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class SkillListener implements Listener {

    private LaBoulangerieMmo laBoulangerieMmo;

    public SkillListener(LaBoulangerieMmo laBoulangerieMmo) {
        this.laBoulangerieMmo = laBoulangerieMmo;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        giveReward(event.getPlayer(), GrindingCategory.BREAK, event.getBlock().getType().toString());
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) return;
        
        giveReward(event.getEntity().getKiller(), GrindingCategory.KILL, event.getEntity().getType().toString());
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!(event.getWhoClicked() instanceof Player)) return;
        Material crafted = event.getRecipe().getResult().getType();

        giveReward(player, GrindingCategory.CRAFT, crafted.toString());

        if (player.getStatistic(Statistic.CRAFT_ITEM, crafted) == 0) {
            giveReward(player, GrindingCategory.FIRST_CRAFT, crafted.toString());
        }
    }

    private void giveReward(Player player, GrindingCategory category, String identifier) {
        if (player.getGameMode() == GameMode.CREATIVE) return;
        Set<String> keys = laBoulangerieMmo.getConfig().getConfigurationSection("talent-grinding").getKeys(false);

        keys.stream().forEach(talentName -> {
            ConfigurationSection section = laBoulangerieMmo.getConfig().getConfigurationSection("talent-grinding." + talentName + "." + category.toString());
            if (section == null) return;

            if (section.getKeys(false).contains(identifier)) laBoulangerieMmo.getMmoPlayerManager().getPlayer(player).incrementXp(talentName, section.getDouble(identifier));
        });
    }
}
