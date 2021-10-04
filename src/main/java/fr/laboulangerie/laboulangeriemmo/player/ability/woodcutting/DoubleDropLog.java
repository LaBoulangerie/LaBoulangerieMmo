package fr.laboulangerie.laboulangeriemmo.player.ability.woodcutting;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class DoubleDropLog extends AbilityExecutor {

	@Override
	public AbilityTrigger getAbilityTrigger() {
		return AbilityTrigger.LEFT_CLICK_BLOCK;
	}

	@Override
	public boolean shouldTrigger(Event baseEvent) {
		  BlockBreakEvent event = (BlockBreakEvent) baseEvent;
	      Block block = event.getBlock();
	      if (event.getBlock().hasMetadata("laboulangerie:placed")) return false;
	      return block != null && block.getType() == Material.OAK_LOG;
	}

	@Override
	public void trigger(Event baseEvent, int level) {

		BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Player player = event.getPlayer();
        ItemStack item = new ItemStack(Material.OAK_LOG);
        	  
        int max = 100;
        int min = 1;
        Random randomNum = new Random();
        int r = min + randomNum.nextInt(max);
        	
        if (level >= 60) {
        	if (r <= 80) {
        		player.getWorld().dropItemNaturally(player.getLocation(), item);
        	}
        }else if (level >= 40) {
        	if (r <= 40) {
        		player.getWorld().dropItemNaturally(player.getLocation(), item);
        	}
        }else {
        	if (r <= 10) {
        		player.getWorld().dropItemNaturally(player.getLocation(), item);
        	}
        }
	}

}
