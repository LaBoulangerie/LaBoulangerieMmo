package net.laboulangerie.laboulangeriemmo.core.abilities.mining;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityTrigger;

public class MinecraftExpMultiplier extends AbilityExecutor {

    public MinecraftExpMultiplier(AbilityArchetype archetype) {
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
        List<Material> ores = Arrays.asList(Material.COAL_ORE, Material.DIAMOND_ORE, Material.REDSTONE_ORE,
                Material.LAPIS_ORE, Material.EMERALD_ORE, Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_DIAMOND_ORE,
                Material.DEEPSLATE_REDSTONE_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_EMERALD_ORE,
                Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE);

        return block != null && !(event.getExpToDrop() == 0) && ores.contains(block.getType());
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        int exp_boost = 0;

        if (level >= 24) {
            exp_boost = (int) (event.getExpToDrop() * 5);
            event.setExpToDrop(exp_boost);
        } else if (level >= 12) {
            exp_boost = (int) (event.getExpToDrop() * 2);
            event.setExpToDrop(exp_boost);
        } else {
            exp_boost = (int) (event.getExpToDrop() * 1.25);
            event.setExpToDrop(exp_boost);
        }
    }
}
