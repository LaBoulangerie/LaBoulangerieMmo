package fr.laboulangerie.laboulangeriemmo.player.ability.mining;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class MinecraftExpMultiplier extends AbilityExecutor{

	@Override
	public AbilityTrigger getAbilityTrigger() {
		return AbilityTrigger.BREAK;
	}

	@Override
	public boolean shouldTrigger(Event baseEvent) {
		BlockBreakEvent event = (BlockBreakEvent) baseEvent;
	      Block block = event.getBlock();
	      if (event.getBlock().hasMetadata("laboulangerie:placed")) return false;
	      return block != null && !(event.getExpToDrop() == 0);
	}

	@Override
	public void trigger(Event baseEvent, int level) {
		BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        int exp_boost = 0;
        
        if (level >= 60) {
        	 exp_boost =  (int) (event.getExpToDrop()*5);
        	 event.setExpToDrop(exp_boost);
        
        }else if (level >= 40) {
        	exp_boost =  (int) (event.getExpToDrop()*2);
        	event.setExpToDrop(exp_boost);
        	
        }else {
        	exp_boost =  (int) (event.getExpToDrop()*1.25);
        	event.setExpToDrop(exp_boost);
		
        }
	}
}
