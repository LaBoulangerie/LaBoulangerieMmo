package net.laboulangerie.laboulangeriemmo.core.abilities.mining;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class MinecraftExpMultiplier extends AbilityExecutor {

    public MinecraftExpMultiplier(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block block = event.getBlock();
        if (LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) != null) return false;
        List<Material> ores =
                Arrays.asList(Material.COAL_ORE, Material.DIAMOND_ORE, Material.REDSTONE_ORE,
                        Material.LAPIS_ORE, Material.EMERALD_ORE, Material.DEEPSLATE_COAL_ORE,
                        Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_REDSTONE_ORE,
                        Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_EMERALD_ORE,
                        Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE);

        return block != null && !(event.getExpToDrop() == 0) && ores.contains(block.getType());
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        int exp_boost = 0;

        if (level >= getTier(2)) {
            exp_boost = (int) (event.getExpToDrop() * 5);
            event.setExpToDrop(exp_boost);
        } else if (level >= getTier(1)) {
            exp_boost = (int) (event.getExpToDrop() * 2);
            event.setExpToDrop(exp_boost);
        } else {
            exp_boost = (int) (event.getExpToDrop() * 1.25);
            event.setExpToDrop(exp_boost);
        }
    }
}
