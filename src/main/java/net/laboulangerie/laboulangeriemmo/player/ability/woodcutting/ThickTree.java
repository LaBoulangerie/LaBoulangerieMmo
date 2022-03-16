package net.laboulangerie.laboulangeriemmo.player.ability.woodcutting;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;


import net.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class ThickTree extends AbilityExecutor{

	@Override
	public AbilityTrigger getAbilityTrigger() {
		return AbilityTrigger.RIGHT_CLICK_BLOCK;
	}

	@Override
	public boolean shouldTrigger(Event baseEvent) {
		PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Block block = event.getClickedBlock();

        return block.getType() != null && Tag.SAPLINGS.isTagged(block.getType()) && event.getPlayer().getItemInHand().getType() == Material.BONE_MEAL && event.getPlayer().isSneaking();
	}

	@Override
	public void trigger(Event baseEvent, int level) {
		PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
		World world = player.getWorld();
		TreeType treeType = null;
		
		if (block.getType() == Material.SPRUCE_SAPLING) {
			treeType = TreeType.MEGA_REDWOOD;
		}
		else if (block.getType() == Material.JUNGLE_SAPLING) {
			treeType = TreeType.JUNGLE;
		}
		else if (block.getType() == Material.DARK_OAK_SAPLING) {
			treeType = TreeType.DARK_OAK;
		}
		else if (block.getType() == Material.OAK_SAPLING) {
			treeType = TreeType.BIG_TREE;
		}
		else if (block.getType() == Material.BIRCH_SAPLING) {
			treeType = TreeType.TALL_BIRCH;
		}
		
		world.generateTree(block.getLocation(), treeType);

	}

}
