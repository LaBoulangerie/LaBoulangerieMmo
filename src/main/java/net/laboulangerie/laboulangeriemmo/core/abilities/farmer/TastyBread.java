package net.laboulangerie.laboulangeriemmo.core.abilities.farmer;

import org.bukkit.Material;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class TastyBread extends AbilityExecutor {

    public TastyBread(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        PlayerItemHeldEvent event = (PlayerItemHeldEvent) baseEvent;
        return event.getPlayer().getInventory().getItem(event.getNewSlot()).getType()
                .equals(Material.BREAD);
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        PlayerItemHeldEvent event = (PlayerItemHeldEvent) baseEvent;
        Player player = event.getPlayer();
        int followRadius = 10;
        if (level >= getTier(1)) {
            followRadius = 20;
        }
        for (Entity en : player.getNearbyEntities(followRadius, followRadius, followRadius)) {
            // Skip sitting animals (tamed wolves, cats, parrots)
            if (en instanceof Sittable && ((Sittable) en).isSitting()) {
                continue;
            }

            boolean shouldAttract = false;
            if (level < getTier(2)) {
                shouldAttract = en.getType() == EntityType.COW
                        || en.getType() == EntityType.PIG
                        || en.getType() == EntityType.CHICKEN
                        || en.getType() == EntityType.SHEEP;
            } else {
                shouldAttract = en instanceof Breedable || en instanceof WaterMob;
            }

            if (shouldAttract && en instanceof Creature) {
                Creature animal = (Creature) en;
                animal.getPathfinder().moveTo(player, 1);
            }
        }
    }
}
