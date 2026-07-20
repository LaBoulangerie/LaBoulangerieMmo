package net.laboulangerie.laboulangeriemmo.core.protection;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.FireBow;

public final class FireBowIgniteListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() != BlockIgniteEvent.IgniteCause.EXPLOSION
                || !FireBow.isFireBowTnt(event.getIgnitingEntity())) {
            return;
        }

        if (!LaBoulangerieMmo.PLUGIN.getTalentProtection().canIgnite(event.getBlock())) {
            event.setCancelled(true);
        }
    }
}
