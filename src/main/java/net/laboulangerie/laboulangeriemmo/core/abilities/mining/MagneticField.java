package net.laboulangerie.laboulangeriemmo.core.abilities.mining;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class MagneticField extends AbilityExecutor {

    public MagneticField(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        ItemStack item = event.getItem();
        return event.getPlayer().isSneaking() && item != null && item.getType() == Material.COMPASS;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Player player = event.getPlayer();
        boolean colorize = false;
        int radius = 5;

        if (level >= getTier(2)) {
            colorize = true;
            radius = 30;
        } else if (level >= getTier(1)) {
            radius = 10;
        }
        new MagneticFieldTask(player.getLocation(), radius, player, colorize)
                .runTask(LaBoulangerieMmo.PLUGIN);
    }
}
