package net.laboulangerie.laboulangeriemmo.core.abilities.farmer;

import org.bukkit.Material;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class TastyBread extends AbilityExecutor{

    public TastyBread(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        PlayerItemHeldEvent event = (PlayerItemHeldEvent) baseEvent;
        return event.getPlayer().getInventory().getItemInMainHand().getType() == Material.BREAD;
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
            if (level < getTier(2)) {
                if (en.getType() == EntityType.COW || en.getType() == EntityType.PIG || en.getType() == EntityType.PIG || en.getType() == EntityType.CHICKEN || en.getType() == EntityType.SHEEP) {
                    Creature animal = (Creature) en;
                    animal.getPathfinder().moveTo(player, 1);
                }
            }
            if (level >= getTier(2)) {
                if (en instanceof Breedable || en instanceof WaterMob) {
                    Creature animal = (Creature) en;
                    animal.getPathfinder().moveTo(player, 1);
                }
            }
        }
    }
}
