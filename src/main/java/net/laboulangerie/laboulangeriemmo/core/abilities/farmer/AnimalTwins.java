package net.laboulangerie.laboulangeriemmo.core.abilities.farmer;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sniffer;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.inventory.ItemStack;

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

        Random rng = new Random();
        double random = rng.nextDouble();

        if (level >= getTier(2)
                || (level >= getTier(1) && random <= 0.4)
                || (random <= 0.1)) {

            if (entity instanceof Sniffer) {
                ItemStack snifferEgg = new ItemStack(Material.SNIFFER_EGG, 1);
                entity.getWorld().dropItem(entity.getLocation(), snifferEgg);
                return;
            }
            Animals animal = (Animals) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
            animal.setBaby();
        }

    }
}
