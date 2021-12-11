package fr.laboulangerie.laboulangeriemmo.player;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.signature.qual.ClassGetName;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import it.unimi.dsi.fastutil.floats.Float2BooleanAVLTreeMap;

public class SkillListener implements Listener {
    public SkillListener() {}

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || event.getBlock().hasMetadata("laboulangerie:placed")) return;
        giveReward(event.getPlayer(), GrindingCategory.BREAK, event.getBlock().getType().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityKill(EntityDeathEvent event) {
        if (event.isCancelled() || !(event.getEntity().getKiller() instanceof Player)) return;
        
        giveReward(event.getEntity().getKiller(), GrindingCategory.KILL, event.getEntity().getType().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.isCancelled() || !(event.getWhoClicked() instanceof Player)) return;
        Material crafted = event.getRecipe().getResult().getType();

        giveReward(player, GrindingCategory.CRAFT, crafted.toString());

        if (player.getStatistic(Statistic.CRAFT_ITEM, crafted) == 0) {
            giveReward(player, GrindingCategory.FIRST_CRAFT, crafted.toString());
        }
    }

    @EventHandler
    public static void onExpBottle(PlayerInteractEvent event) {
    	Player player = (Player) event.getPlayer();
    	if (event.getAction().isRightClick() && !(player.getItemInHand().getItemMeta() == null)) {
    	
    	ItemMeta im = player.getItemInHand().getItemMeta();
    	if (im.getDisplayName().equals("§aBouteille de 1 Level")) {
    		event.setCancelled(true);
    		player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
            
    		event.getPlayer().getWorld().spawn(event.getPlayer().getLocation(), ExperienceOrb.class).setExperience(player.getExpToLevel());
    		Float playerExp = player.getExp();
    		player.setExp(playerExp);
    	}
    	}
    }

    private void giveReward(Player player, GrindingCategory category, String identifier) {
        if (player.getGameMode() == GameMode.CREATIVE) return;
        Set<String> keys = LaBoulangerieMmo.PLUGIN.getConfig().getConfigurationSection("talent-grinding").getKeys(false);

        keys.stream().forEach(talentName -> {
            ConfigurationSection section = LaBoulangerieMmo.PLUGIN.getConfig().getConfigurationSection("talent-grinding." + talentName + "." + category.toString());
            if (section == null) return;

            if (section.getKeys(false).contains(identifier)) LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player).incrementXp(talentName, section.getDouble(identifier));
        });
        
    }

}
