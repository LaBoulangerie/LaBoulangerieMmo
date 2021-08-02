package fr.laboulangerie.laboulangeriemmo.player.ability;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class AbilitiesManager implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
                Abilities.stream()
                    .filter(x -> 
                        x.getExecutor().getAbilityTrigger() == AbilityTrigger.CLICK_AIR
                        && x.getExecutor().shouldTrigger(event)
                    )
                    .forEach(x -> x.getExecutor().trigger(event));
                break;
            case LEFT_CLICK_BLOCK:
                Abilities.stream()
                    .filter(x -> 
                        x.getExecutor().getAbilityTrigger() == AbilityTrigger.CLICK_BLOCK
                        && x.getExecutor().shouldTrigger(event)
                    )
                    .forEach(x -> x.getExecutor().trigger(event));
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onConsumeItem(PlayerItemConsumeEvent event) {
        Abilities.stream()
            .filter(x -> 
                x.getExecutor().getAbilityTrigger() == AbilityTrigger.EAT
                && x.getExecutor().shouldTrigger(event)
            )
            .forEach(x -> x.getExecutor().trigger(event));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Abilities.stream()
            .filter(x -> 
                x.getExecutor().getAbilityTrigger() == AbilityTrigger.PLACE
                && x.getExecutor().shouldTrigger(event)
            )
            .forEach(x -> x.getExecutor().trigger(event));
    }

    @EventHandler
    public void onClickEntity(PlayerInteractEntityEvent event) {
        Abilities.stream()
            .filter(x -> 
                x.getExecutor().getAbilityTrigger() == AbilityTrigger.CLICK_ENTITY
                && x.getExecutor().shouldTrigger(event)
            )
            .forEach(x -> x.getExecutor().trigger(event));
    }
}
