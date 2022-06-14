package net.laboulangerie.laboulangeriemmo.abilities;

import java.lang.reflect.InvocationTargetException;

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
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
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
        LaBoulangerieMmo.talentsRegistry.getTalents().values().forEach(talentArchetype -> {
            if (trigger == AbilityTrigger.COMBO && talentArchetype.comboItems != null //Player doesn't have the right item in his hand so the combo is refused
                && !talentArchetype.comboItems.contains(((ComboCompletedEvent) event).getPlayer().getInventory().getItemInMainHand().getType())) {
                    return;
            }
            talentArchetype.abilitiesArchetypes.values().stream()
            .filter(abilityArchetype -> LaBoulangerieMmo.abilitiesRegistry.getTriggerForAbility(abilityArchetype.identifier) == trigger)
            .filter(abilityArchetype -> player.canUseAbility(abilityArchetype, talentArchetype.identifier))
            .forEach(abilityArchetype -> {
                AbilityExecutor executor;
                try {
                    executor = LaBoulangerieMmo.abilitiesRegistry.newAbilityExecutor(abilityArchetype);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {

                    Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
                    bukkitPlayer.sendMessage("§4An error occurred when trying to execute the ability '"+ abilityArchetype.identifier +"', please report this to an administrator!");
                    LaBoulangerieMmo.PLUGIN.getLogger().severe("An error occurred when trying to execute the ability '"+ abilityArchetype.identifier +"' for player '"+ bukkitPlayer.getName() +"'!");
                    e.printStackTrace();
                    return;
                }
                if (executor.shouldTrigger(event)) {
                    executor.trigger(event, player.getTalent(talentArchetype.identifier).getLevel(LaBoulangerieMmo.XP_MULTIPLIER));
                    player.useAbility(abilityArchetype, talentArchetype);
                    Bukkit.getPluginManager().callEvent(new MmoPlayerUseAbilityEvent(player, abilityArchetype, executor, event, trigger));
                }
            });
        });
    }
}
