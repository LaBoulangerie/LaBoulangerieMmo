package fr.laboulangerie.laboulangeriemmo.player.ability.woodcutting;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;
import fr.laboulangerie.laboulangeriemmo.player.ability.ParticulEffect;

public class DoubleDropLog extends AbilityExecutor {

	@Override
	public AbilityTrigger getAbilityTrigger() {
		return AbilityTrigger.BREAK;
	}

	@Override
	public boolean shouldTrigger(Event baseEvent) {
		  BlockBreakEvent event = (BlockBreakEvent) baseEvent;
	      Block block = event.getBlock();
	      if (event.getBlock().hasMetadata("laboulangerie:placed")) return false;
	      return block != null && Tag.LOGS.isTagged(block.getType());

	}

	@Override
	public void trigger(Event baseEvent, int level) {

		BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block block = event.getBlock();
        ItemStack item = new ItemStack(block.getType());
        Player player = event.getPlayer();

        	  
        int max_number = 100;
        int min_number = 1;
        Random random_chance = new Random();
        int find_nearest_int = min_number + random_chance.nextInt(max_number);
        	
        if (level >= 60) {
        	if (find_nearest_int <= 80) {
        		new ParticulEffect().createHelix(player);
        		block.getWorld().dropItemNaturally(block.getLocation(), item);
        		player.sendMessage(ChatColor.GOLD + "[LaBoulangerieMmo] " + ChatColor.YELLOW + "Votre drop a �t� doubl�.");
        	}
        }else if (level >= 40) {
        	if (find_nearest_int <= 40) {
        		new ParticulEffect().createHelix(player);
        		block.getWorld().dropItemNaturally(block.getLocation(), item);
        		player.sendMessage(ChatColor.GOLD + "[LaBoulangerieMmo] " + ChatColor.YELLOW + "Votre drop a �t� doubl�.");
        	}
        }else {
        	if (find_nearest_int <= 10) {
        		new ParticulEffect().createHelix(player);
        		block.getWorld().dropItemNaturally(block.getLocation(), item);
        		player.sendMessage(ChatColor.GOLD + "[LaBoulangerieMmo] " + ChatColor.YELLOW + "Votre drop a �t� doubl�.");
        	}
        }
	}

}
