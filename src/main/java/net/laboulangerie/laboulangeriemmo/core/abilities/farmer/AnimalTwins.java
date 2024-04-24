package net.laboulangerie.laboulangeriemmo.core.abilities.farmer;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityBreedEvent;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class AnimalTwins extends AbilityExecutor {

    private final static float TIER_3_CHANCE = 1f;
    private final static float TIER_2_CHANCE = 0.4f;
    private final static float TIER_1_CHANCE = 0.1f;

    public AnimalTwins(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        return true;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        EntityBreedEvent event = (EntityBreedEvent) baseEvent;
        Entity entity = event.getEntity();

        float random = (float) Math.random();

        if (level >= getTier(2) && random <= TIER_3_CHANCE
                || (level >= getTier(1) && random <= TIER_2_CHANCE)
                || (random <= TIER_1_CHANCE)) {
            Animals animal = (Animals) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
            animal.setBaby();
        }
    }
}
