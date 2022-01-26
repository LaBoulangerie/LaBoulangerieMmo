package net.laboulangerie.laboulangeriemmo.player.ability.farmer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.events.MmoPlayerBreakBlockEvent;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class NatureTouch extends AbilityExecutor{

	@Override
	public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.BREAK;

	}

	@Override
	public boolean shouldTrigger(Event baseEvent) {
		BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block block = event.getBlock();
        return block.getType() == Material.NETHER_WART || Tag.CROPS.isTagged(block.getType());
	}

	@Override
	public void trigger(Event baseEvent, int level) {
		BlockBreakEvent event = (BlockBreakEvent) baseEvent;
		final Block block = event.getBlock();
        Material cropBlockType = null;
        Material seedBlockType = null;
        if (block.getType() == Material.WHEAT_SEEDS || block.getType() == Material.WHEAT) {
          cropBlockType = Material.WHEAT;
          seedBlockType = Material.WHEAT_SEEDS;
        } else if (block.getType() == Material.POTATO || block.getType() == Material.POTATOES) {
          cropBlockType = Material.POTATOES;
          seedBlockType = Material.POTATO;
        } else if (block.getType() == Material.CARROT || block.getType() == Material.CARROTS) {
          cropBlockType = Material.CARROTS;
          seedBlockType = Material.CARROT;
        } else if (block.getType() == Material.NETHER_WART) {
          cropBlockType = Material.NETHER_WART;
          seedBlockType = Material.NETHER_WART;
        } else {
          return;
        } 
        Ageable ageable = (Ageable)block.getBlockData();
        if (ageable.getAge() == 0) {
          block.getDrops().clear();
          event.setCancelled(true);
        } else {
          final Material finalCropBlockType = cropBlockType;
          Bukkit.getScheduler().runTaskLater((Plugin)LaBoulangerieMmo.PLUGIN, new Runnable() {
                public void run() {
                  block.setType(finalCropBlockType);
                }
              },  1L);          
          } 
          event.setCancelled(false);
        
        }
	
}
