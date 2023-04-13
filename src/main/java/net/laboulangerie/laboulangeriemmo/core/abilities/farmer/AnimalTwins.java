package net.laboulangerie.laboulangeriemmo.core.abilities.farmer;

import java.util.Random;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityBreedEvent;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class AnimalTwins extends AbilityExecutor {

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

        int max_number = 100;
        int min_number = 1;
        Random random_chance = new Random();
        int find_nearest_int = min_number + random_chance.nextInt(max_number);

        if (level >= getTier(2)) {
            Animals animal =
                    (Animals) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
            animal.setBaby();
        } else if (level >= getTier(1) && find_nearest_int <= 40) {
            Animals animal =
                    (Animals) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
            animal.setBaby();
        } else if (find_nearest_int <= 10) {
            Animals animal =
                    (Animals) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
            animal.setBaby();
        }

    }
}
