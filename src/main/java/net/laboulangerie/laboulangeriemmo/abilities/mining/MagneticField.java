package net.laboulangerie.laboulangeriemmo.abilities.mining;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.abilities.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.abilities.AbilityTrigger;
import net.laboulangerie.laboulangeriemmo.core.MagneticFieldTask;

public class MagneticField extends AbilityExecutor {

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.RIGHT_CLICK_AIR;
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

        if (level >= 95) {
            colorize = true;
            radius = 30;
        } else if (level >= 65) {
            radius = 10;
        }
        new MagneticFieldTask(player.getLocation(), radius, player, colorize).runTask(LaBoulangerieMmo.PLUGIN);
    }
}
