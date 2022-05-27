package net.laboulangerie.laboulangeriemmo.player.ability.farmer;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

import net.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class TastyBread extends AbilityExecutor{

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.HOLD_ITEM;
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
        if (level > 30) {
            followRadius = 20;
        }
        for (Entity en : player.getNearbyEntities(followRadius, followRadius, followRadius)) {
            if (level < 45) {
                if (en.getType() == EntityType.COW || en.getType() == EntityType.PIG || en.getType() == EntityType.PIG || en.getType() == EntityType.CHICKEN || en.getType() == EntityType.SHEEP) {
                    Creature animal = (Creature) en;
                    animal.getPathfinder().moveTo(player, 1);
                }
            }
            if (level >= 45) {
                if (en.getType() == EntityType.COW || en.getType() == EntityType.PIG || en.getType() == EntityType.PIG || en.getType() == EntityType.CHICKEN || en.getType() == EntityType.SHEEP || en.getType() == EntityType.VILLAGER || en.getType() == EntityType.AXOLOTL || en.getType() == EntityType.CAT  || en.getType() == EntityType.WOLF  || en.getType() == EntityType.PANDA || en.getType() == EntityType.RABBIT || en.getType() == EntityType.HORSE || en.getType() == EntityType.BEE || en.getType() == EntityType.FOX || en.getType() == EntityType.GOAT || en.getType() == EntityType.LLAMA || en.getType() == EntityType.TURTLE || en.getType() == EntityType.MULE || en.getType() == EntityType.TRADER_LLAMA || en.getType() == EntityType.DONKEY || en.getType() == EntityType.STRIDER || en.getType() == EntityType.OCELOT || en.getType() == EntityType.PARROT || en.getType() == EntityType.SQUID || en.getType() == EntityType.GLOW_SQUID || en.getType() == EntityType.POLAR_BEAR || en.getType() == EntityType.BAT || en.getType() == EntityType.DOLPHIN || en.getType() == EntityType.WANDERING_TRADER || en.getType() == EntityType.MUSHROOM_COW || en.getType() == EntityType.SKELETON_HORSE || en.getType() == EntityType.ZOMBIE_HORSE) {
                    Creature animal = (Creature) en;
                    animal.getPathfinder().moveTo(player, 1);
                }
            }
        }

    }

}
