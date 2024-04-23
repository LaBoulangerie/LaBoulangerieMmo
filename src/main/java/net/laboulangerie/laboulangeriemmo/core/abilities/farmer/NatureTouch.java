package net.laboulangerie.laboulangeriemmo.core.abilities.farmer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Bisected;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class NatureTouch extends AbilityExecutor {

    private final static float TIER_3_CHANCE = 1f;
    private final static float TIER_2_CHANCE = 0.4f;
    private final static float TIER_1_CHANCE = 0.1f;

    public NatureTouch(AbilityArchetype archetype) {
        super(archetype);
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
        Material cropMaterial = block.getType();
        Ageable ageable = (Ageable) block.getBlockData();

        // Crop didnt finish to grow
        if (ageable.getAge() != ageable.getMaximumAge())
            return;

        float random = (float) Math.random();
        boolean shouldReplant = false;

        if (level >= getTier(2) && random <= TIER_3_CHANCE)
            shouldReplant = true;
        else if (level >= getTier(1) && random <= TIER_2_CHANCE)
            shouldReplant = true;
        else if (random <= TIER_1_CHANCE)
            shouldReplant = true;

        if (shouldReplant) {
            Bukkit.getScheduler().runTaskLater(LaBoulangerieMmo.PLUGIN, new Runnable() {
                public void run() {
                    Block updatedBlock = block;
                    if (block.getBlockData() instanceof Bisected) {
                        Bisected bisected = (Bisected) block.getBlockData();
                        if (bisected.getHalf() == Bisected.Half.TOP) {
                            updatedBlock = block.getRelative(0, -1, 0);
                        }
                    }

                    updatedBlock.setType(cropMaterial);
                    updatedBlock.getWorld().spawnParticle(
                            Particle.VILLAGER_HAPPY,
                            updatedBlock.getLocation().toCenterLocation().add(0, -0.2, 0),
                            5, 0.1, 0.1, 0.1);
                }
            }, 1L);
        }
    }
}
