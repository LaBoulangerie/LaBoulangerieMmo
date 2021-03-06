package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import java.util.Random;

import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;

public class DoubleDropLog extends AbilityExecutor {

    public DoubleDropLog(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block block = event.getBlock();
        if (event.getBlock().hasMetadata("laboulangerie:placed"))
            return false;
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
        boolean shouldDouble = false;

        if (level >= getTier(2) && find_nearest_int <= 80)
            shouldDouble = true;
        else if (level >= getTier(1) && find_nearest_int <= 40)
            shouldDouble = true;
        else if (find_nearest_int <= 10)
            shouldDouble = true;

        if (shouldDouble) {
            EffectRegistry.playEffect("default", player);
            block.getWorld().dropItemNaturally(block.getLocation(), item);
            player.sendMessage("§eVotre drop a été doublé.");
        }
    }
}
