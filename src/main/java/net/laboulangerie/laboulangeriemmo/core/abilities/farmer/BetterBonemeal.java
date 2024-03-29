package net.laboulangerie.laboulangeriemmo.core.abilities.farmer;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class BetterBonemeal extends AbilityExecutor {

    public BetterBonemeal(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        ItemStack item = event.getItem();
        return event.getPlayer().isSneaking() && item != null
                && item.getType() == Material.BONE_MEAL;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        if (level >= getTier(2)) {
            ArrayList<Block> blocks =
                    getBlocksAroundCenter(event.getClickedBlock().getLocation(), 20);
            for (Block block : blocks) {
                block.applyBoneMeal(BlockFace.UP);
                block.applyBoneMeal(BlockFace.UP);
                block.applyBoneMeal(BlockFace.UP);
            }
        } else if (level >= getTier(1) && level < 45) {
            ArrayList<Block> blocks =
                    getBlocksAroundCenter(event.getClickedBlock().getLocation(), 10);
            for (Block block : blocks) {
                block.applyBoneMeal(BlockFace.UP);
                block.applyBoneMeal(BlockFace.UP);
            }
        } else {
            ArrayList<Block> blocks =
                    getBlocksAroundCenter(event.getClickedBlock().getLocation(), 5);
            for (Block block : blocks) {
                block.applyBoneMeal(BlockFace.UP);
            }
        }
    }

    public static ArrayList<Block> getBlocksAroundCenter(Location loc, int radius) {
        ArrayList<Block> blocks = new ArrayList<Block>();

        for (int x = (loc.getBlockX() - radius); x <= (loc.getBlockX() + radius); x++) {
            for (int y = (loc.getBlockY() - radius); y <= (loc.getBlockY() + radius); y++) {
                for (int z = (loc.getBlockZ() - radius); z <= (loc.getBlockZ() + radius); z++) {
                    Location l = new Location(loc.getWorld(), x, y, z);
                    if (l.distance(loc) <= radius) {
                        Block block = l.getBlock();
                        if (Tag.CROPS.isTagged(block.getType())) blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }
}
