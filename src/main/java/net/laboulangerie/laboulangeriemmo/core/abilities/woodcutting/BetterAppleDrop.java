package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityTrigger;

public class BetterAppleDrop extends AbilityExecutor {
    public BetterAppleDrop(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.BREAK;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block block = event.getBlock();
        if (event.getBlock().hasMetadata("laboulangerie:placed"))
            return false;
        return block != null && block.getType() == Material.OAK_LEAVES;
    }

    @Override
    public void trigger(Event baseEvent, int level) {

        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block block = event.getBlock();
        ItemStack item = new ItemStack(Material.APPLE);

        int max_number = 100;
        int min_number = 1;
        Random random_chance = new Random();
        int find_nearest_int = min_number + random_chance.nextInt(max_number);
        boolean shouldDrop = false;

        if (level >= 60 && find_nearest_int <= 80)
            shouldDrop = true;
        else if (level >= 40 && find_nearest_int <= 40)
            shouldDrop = true;
        else if (find_nearest_int <= 10)
            shouldDrop = true;

        if (shouldDrop) {
            block.getWorld().dropItemNaturally(block.getLocation(), item);
        }
    }

}
