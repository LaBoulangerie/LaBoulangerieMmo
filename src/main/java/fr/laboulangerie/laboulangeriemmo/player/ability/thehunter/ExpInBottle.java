package fr.laboulangerie.laboulangeriemmo.player.ability.thehunter;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.laboulangerie.laboulangeriemmo.player.SkillListener;
import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class ExpInBottle extends AbilityExecutor{

	@Override
	public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.RIGHT_CLICK_AIR;
	}

	@Override
	public boolean shouldTrigger(Event baseEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        ItemStack item = event.getItem();
        return event.getPlayer().isSneaking() && item != null && item.getType() == Material.GLASS_BOTTLE;
	}

	@Override
	public void trigger(Event baseEvent, int level) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Player player = event.getPlayer();
        if (player.getLevel() >= 1) {
        	Integer playerCurrentLevel = player.getLevel();
        	player.setLevel(playerCurrentLevel - 1);
        	ItemStack  item = new ItemStack(Material.EXPERIENCE_BOTTLE);
        	ItemMeta itemMeta = item.getItemMeta();
        	itemMeta.setDisplayName("§aBouteille de 1 Level");
        	item.setItemMeta(itemMeta);
        	player.getInventory().addItem(new ItemStack(item));
        }
		
	}

}
