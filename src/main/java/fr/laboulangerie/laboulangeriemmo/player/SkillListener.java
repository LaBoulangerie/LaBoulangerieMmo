package fr.laboulangerie.laboulangeriemmo.player;

import java.util.Set;
import java.util.function.BiConsumer;

import org.bukkit.GameMode;
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
        MmoPlayer player = laBoulangerieMmo.getMmoPlayerManager().getPlayer(event.getPlayer());
        giveReward(event.getPlayer(), GrindingCategory.BREAK, event.getBlock().getType().toString(), (talentName, amount) -> player.incrementXp(talentName, amount));
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) return;

        Player bukkitPlayer = event.getEntity().getKiller();
        MmoPlayer player = laBoulangerieMmo.getMmoPlayerManager().getPlayer(bukkitPlayer);
        
        giveReward(bukkitPlayer, GrindingCategory.KILL, event.getEntity().getType().toString(),
            (talentName, amount) -> player.incrementXp(talentName, amount));
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Player bukkitPlayer = (Player) event.getWhoClicked();
        MmoPlayer player = laBoulangerieMmo.getMmoPlayerManager().getPlayer(bukkitPlayer);

        giveReward(bukkitPlayer, GrindingCategory.CRAFT, event.getRecipe().getResult().getType().toString(),
            (talentName, amount) -> player.incrementXp(talentName, amount));
    }

    @EventHandler
    public void onRecipeDiscover(PlayerRecipeDiscoverEvent event) {
        Player bukkitPlayer = event.getPlayer();
        MmoPlayer player = laBoulangerieMmo.getMmoPlayerManager().getPlayer(bukkitPlayer);

        giveReward(bukkitPlayer, GrindingCategory.DISCOVER_RECIPE, event.getRecipe().getKey(),
            (talentName, amount) -> player.incrementXp(talentName, amount));
    }

    private void giveReward(Player player, GrindingCategory category, String identifier, BiConsumer<String, Double> consumer) {
        if (player.getGameMode() == GameMode.CREATIVE) return;
        Set<String> keys = laBoulangerieMmo.getConfig().getConfigurationSection("talent-grinding").getKeys(false);

        keys.stream().forEach(talentName -> {
            ConfigurationSection section = laBoulangerieMmo.getConfig().getConfigurationSection("talent-grinding." + talentName + "." + category.toString());
            if (section == null) return;

            if (section.getKeys(false).contains(identifier)) consumer.accept(talentName, section.getDouble(identifier));
        });
    }
}
