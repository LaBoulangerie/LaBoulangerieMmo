package fr.laboulangerie.laboulangeriemmo.player.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayerManager;

public class AbilitiesManager implements Listener {

    private LaBoulangerieMmo plugin;
    private MmoPlayerManager players;

    public AbilitiesManager(LaBoulangerieMmo plugin) {
        this.plugin = plugin;
        this.players = plugin.getMmoPlayerManager();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
                Abilities.supplier().get()
                    .filter(x ->
                        players.getPlayer(event.getPlayer()).getTalent(x.getParentTalent()).getLevel(0) >= x.getRequiredLevel()
                        && x.getExecutor().getAbilityTrigger() == AbilityTrigger.LEFT_CLICK_AIR
                        && x.getExecutor().shouldTrigger(event)
                    )
                    .forEach(x -> x.getExecutor().trigger(event));
                break;
            case RIGHT_CLICK_AIR:
                Abilities.supplier().get()
                    .filter(x ->
                        players.getPlayer(event.getPlayer()).getTalent(x.getParentTalent()).getLevel(0) >= x.getRequiredLevel()
                        && x.getExecutor().getAbilityTrigger() == AbilityTrigger.RIGHT_CLICK_AIR
                        && x.getExecutor().shouldTrigger(event)
                    )
                    .forEach(x -> x.getExecutor().trigger(event));
                break;
            case LEFT_CLICK_BLOCK:
                Abilities.supplier().get()
                    .filter(x -> 
                        players.getPlayer(event.getPlayer()).getTalent(x.getParentTalent()).getLevel(0) >= x.getRequiredLevel()
                        && x.getExecutor().getAbilityTrigger() == AbilityTrigger.LEFT_CLICK_BLOCK
                        && x.getExecutor().shouldTrigger(event)
                    )
                    .forEach(x -> x.getExecutor().trigger(event));
                break;
            case RIGHT_CLICK_BLOCK:
                Abilities.supplier().get()
                    .filter(x -> 
                        players.getPlayer(event.getPlayer()).getTalent(x.getParentTalent()).getLevel(0) >= x.getRequiredLevel()
                        && x.getExecutor().getAbilityTrigger() == AbilityTrigger.RIGHT_CLICK_BLOCK
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
        Abilities.supplier().get()
            .filter(x -> 
                players.getPlayer(event.getPlayer()).getTalent(x.getParentTalent()).getLevel(0) >= x.getRequiredLevel()
                && x.getExecutor().getAbilityTrigger() == AbilityTrigger.EAT
                && x.getExecutor().shouldTrigger(event)
            )
            .forEach(x -> x.getExecutor().trigger(event));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Abilities.supplier().get()
            .filter(x -> 
                players.getPlayer(event.getPlayer()).getTalent(x.getParentTalent()).getLevel(0) >= x.getRequiredLevel()
                && x.getExecutor().getAbilityTrigger() == AbilityTrigger.PLACE
                && x.getExecutor().shouldTrigger(event)
            )
            .forEach(x -> x.getExecutor().trigger(event));
    }

    @EventHandler
    public void onClickEntity(PlayerInteractEntityEvent event) {
        Abilities.supplier().get()
            .filter(x -> 
                players.getPlayer(event.getPlayer()).getTalent(x.getParentTalent()).getLevel(0) >= x.getRequiredLevel()
                && x.getExecutor().getAbilityTrigger() == AbilityTrigger.RIGHT_CLICK_ENTITY
                && x.getExecutor().shouldTrigger(event)
            )
            .forEach(x -> x.getExecutor().trigger(event));
    }

    @EventHandler
    public void onHurtEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Abilities.supplier().get()
                .filter(x -> 
                    players.getPlayer((Player) event.getDamager()).getTalent(x.getParentTalent()).getLevel(0) >= x.getRequiredLevel()
                    && x.getExecutor().getAbilityTrigger() == AbilityTrigger.LEFT_CLICK_ENTITY
                    && x.getExecutor().shouldTrigger(event)
                )
                .forEach(x -> x.getExecutor().trigger(event));
        }
    }
}
