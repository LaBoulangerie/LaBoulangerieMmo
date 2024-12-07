package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class DoubleDropLog extends AbilityExecutor {

    private final static float TIER_3_CHANCE = 0.8f;
    private final static float TIER_2_CHANCE = 0.4f;
    private final static float TIER_1_CHANCE = 0.1f;

    public DoubleDropLog(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block block = event.getBlock();
        if (LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) != null) return false;
        return block != null && (Tag.LOGS.isTagged(block.getType()) || block.getType() == Material.OAK_LEAVES);
    }

    @Override
    public void trigger(Event baseEvent, int level) {

        BlockBreakEvent event = (BlockBreakEvent) baseEvent;
        Block block = event.getBlock();
        ItemStack droppedItem = new ItemStack(block.getType());

        if (block.getType() == Material.OAK_LEAVES) droppedItem = new ItemStack(Material.APPLE);

        float random = (float) Math.random();
        boolean shouldDrop = false;

        if (level >= getTier(2) && random <= TIER_3_CHANCE) shouldDrop = true;
        else if (level >= getTier(1) && random <= TIER_2_CHANCE) shouldDrop = true;
        else if (random <= TIER_1_CHANCE) shouldDrop = true;

        if (shouldDrop) {
            Location location = block.getLocation();
            location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location.toCenterLocation().add(0, -0.2, 0), 5,
                    0.1, 0.1, 0.1);
            location.getWorld().dropItemNaturally(location, droppedItem);
        }
    }
}
