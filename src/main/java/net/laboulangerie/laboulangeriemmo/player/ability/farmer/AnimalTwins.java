package net.laboulangerie.laboulangeriemmo.player.ability.farmer;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityBreedEvent;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class AnimalTwins extends AbilityExecutor{

	@Override
	public AbilityTrigger getAbilityTrigger() {
		return AbilityTrigger.RIGHT_CLICK_ENTITY;
	}

	@Override
	public boolean shouldTrigger(Event baseEvent) {
		return true;
	}

	@Override
	public void trigger(Event baseEvent, int level) {
		EntityBreedEvent event = (EntityBreedEvent) baseEvent;
		Entity animal = event.getEntity();
		

        int max_number = 100;
        int min_number = 1;
        Random random_chance = new Random();
        int find_nearest_int = min_number + random_chance.nextInt(max_number);
        if (level >= 60) {
        	animal.getWorld().spawnEntity(animal.getLocation(), animal.getType());
        }
        else if (level >= 40 && find_nearest_int <= 40) {
        	animal.getWorld().spawnEntity(animal.getLocation(), animal.getType());
        }
        else if (find_nearest_int <= 10) {
        	animal.getWorld().spawnEntity(animal.getLocation(), animal.getType());
        }
    
	}

}
