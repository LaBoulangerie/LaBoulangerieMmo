package net.laboulangerie.laboulangeriemmo.core.abilities.farmer;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
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
        Player player = event.getPlayer();
        int radius;
        int applications;

        if (level >= getTier(2)) {
            radius = 20;
            applications = 3;
        } else if (level >= getTier(1) && level < 45) {
            radius = 10;
            applications = 2;
        } else {
            radius = 5;
            applications = 1;
        }

        ArrayList<Block> blocks = getBlocksAroundCenter(event.getClickedBlock().getLocation(), radius);
        for (Block block : blocks) {
            if (!LaBoulangerieMmo.PLUGIN.getTalentProtection()
                    .canPlace(player, block, block.getType())) {
                continue;
            }

            for (int application = 0; application < applications; application++) {
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
