package net.laboulangerie.laboulangeriemmo.abilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityTrigger;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;
import net.laboulangerie.laboulangeriemmo.events.MmoPlayerUseAbilityEvent;

public class AbilitiesManager implements Listener {

    public AbilitiesManager() {
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        MmoPlayer player = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer());

        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
                triggerAbility(player, event, AbilityTrigger.LEFT_CLICK_AIR);
                break;
            case RIGHT_CLICK_AIR:
                triggerAbility(player, event, AbilityTrigger.RIGHT_CLICK_AIR);
                break;
            case LEFT_CLICK_BLOCK:
                triggerAbility(player, event, AbilityTrigger.LEFT_CLICK_BLOCK);
                break;
            case RIGHT_CLICK_BLOCK:
                triggerAbility(player, event, AbilityTrigger.RIGHT_CLICK_BLOCK);
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onConsumeItem(PlayerItemConsumeEvent event) {
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event, AbilityTrigger.EAT);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event, AbilityTrigger.PLACE);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event, AbilityTrigger.BREAK);
    }

    @EventHandler
    public void onClickEntity(PlayerInteractEntityEvent event) {
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event, AbilityTrigger.RIGHT_CLICK_ENTITY);
    }

    @EventHandler
    public void onHurtEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player)
            triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer((Player) event.getDamager()), event, AbilityTrigger.LEFT_CLICK_ENTITY);
    }

    @EventHandler
    public void onComboCompleted(ComboCompletedEvent event) {
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event, AbilityTrigger.COMBO);
    }
    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer((Player) event.getBreeder()), event, AbilityTrigger.BREED);
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event, AbilityTrigger.HOLD_ITEM);
    }

    public void triggerAbility(MmoPlayer player, Event event, AbilityTrigger trigger) {
        Abilities.supplier().get()
        .filter(x -> x.getExecutor().getAbilityTrigger() == trigger
        && player.canUseAbility(x)
        && x.getExecutor().shouldTrigger(event))
        .forEach(x -> {
            x.getExecutor().trigger(event,
                    player.getTalent(x.getParentTalent()).getLevel(LaBoulangerieMmo.XP_MULTIPLIER)
            );
            player.useAbility(x);
            Bukkit.getPluginManager().callEvent(new MmoPlayerUseAbilityEvent(player, x, event));
        });
        LaBoulangerieMmo.talentsRegistry.getTalents()
        .values().stream().forEach(archetype -> {
            
        });
    }
}
