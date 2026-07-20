package net.laboulangerie.laboulangeriemmo.core.abilities;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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
import net.laboulangerie.laboulangeriemmo.utils.WolrdGuardSupport;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class AbilitiesDispatcher implements Listener {

    public AbilitiesDispatcher() {}

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Result.DENY && event.useItemInHand() == Result.DENY) return;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onConsumeItem(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event,
                AbilityTrigger.EAT);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event,
                AbilityTrigger.PLACE);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event,
                AbilityTrigger.BREAK);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClickEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event,
                AbilityTrigger.RIGHT_CLICK_ENTITY);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHurtEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && !event.isCancelled())
            triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer((Player) event.getDamager()), event,
                    AbilityTrigger.LEFT_CLICK_ENTITY);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onComboCompleted(ComboCompletedEvent event) {
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event,
                AbilityTrigger.COMBO);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityBreed(EntityBreedEvent event) {
        if (event.isCancelled() || event.getBreeder() == null || !(event.getBreeder() instanceof Player)) return;
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer((Player) event.getBreeder()), event,
                AbilityTrigger.BREED);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeld(PlayerItemHeldEvent event) {
        if (event.isCancelled()) return;
        if (event.getPlayer().getInventory().getItem(event.getNewSlot()) == null) return;
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(event.getPlayer()), event,
                AbilityTrigger.HOLD_ITEM);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        triggerAbility(LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(killer), event,
                AbilityTrigger.ENTITY_DEATH);
    }

    public void triggerAbility(MmoPlayer player, Event event, AbilityTrigger trigger) {
        if (LaBoulangerieMmo.WORLDGUARD_SUPPORT
                && !WolrdGuardSupport.isOperationPermitted(Bukkit.getPlayer(player.getUniqueId())))
            return;

        LaBoulangerieMmo.talentsRegistry.getTalents().values().forEach(talentArchetype -> {
            // Player doesn't have the right item in his hand so the combo is refused
            if (trigger == AbilityTrigger.COMBO
                    && (talentArchetype.comboItems == null || !talentArchetype.comboItems
                            .contains(((ComboCompletedEvent) event).getPlayer().getInventory().getItemInMainHand()
                                    .getType()))) {
                return;
            }
            talentArchetype.abilitiesArchetypes.values().stream()
                    .filter(abilityArchetype -> LaBoulangerieMmo.abilitiesRegistry
                            .getTriggerForAbility(abilityArchetype.identifier) == trigger)
                    .forEach(abilityArchetype -> {
                        Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
                        if (bukkitPlayer == null || bukkitPlayer.getGameMode() == GameMode.CREATIVE
                                || player.getTalent(talentArchetype.identifier).getLevel()
                                        < abilityArchetype.requiredLevel) {
                            return;
                        }

                        AbilityExecutor executor;
                        try {
                            executor = LaBoulangerieMmo.abilitiesRegistry.newAbilityExecutor(abilityArchetype);
                        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                                | InvocationTargetException | NoSuchMethodException | SecurityException e) {

                            bukkitPlayer.sendMessage("§4An error occurred when trying to execute the ability '"
                                    + abilityArchetype.identifier + "', please report this to an administrator!");
                            LaBoulangerieMmo.PLUGIN.getLogger()
                                    .severe("An error occurred when trying to execute the ability '"
                                            + abilityArchetype.identifier + "' for player '" + bukkitPlayer.getName()
                                            + "'!");
                            e.printStackTrace();
                            return;
                        }
                        if (executor.shouldTrigger(event)) {
                            if (!player.getCooldowns().isCooldownElapsed(
                                    abilityArchetype, talentArchetype.identifier)) {
                                if (trigger == AbilityTrigger.COMBO) {
                                    long elapsed = player.getCooldowns().getCooldown(
                                            abilityArchetype, talentArchetype.identifier);
                                    long remaining = remainingCooldown(abilityArchetype.cooldown, elapsed);
                                    bukkitPlayer.sendMessage(MiniMessage.miniMessage().deserialize(
                                            LaBoulangerieMmo.PLUGIN.getConfig().getString(
                                                    "lang.messages.ability-use-cooldown",
                                                    "<gold>You must wait <gray><duration> <unit></gray> before using <aqua><ability></aqua>."),
                                            TagResolver.resolver(
                                                    Placeholder.parsed("ability", abilityArchetype.displayName),
                                                    Placeholder.parsed("duration", Long.toString(remaining)),
                                                    Placeholder.parsed("unit", abilityArchetype.cooldownUnit
                                                            .toString().toLowerCase()),
                                                    Placeholder.parsed("talent", talentArchetype.displayName))));
                                }
                                return;
                            }
                            executor.trigger(event, player.getTalent(talentArchetype.identifier).getLevel());
                            player.useAbility(abilityArchetype, talentArchetype);
                            Bukkit.getPluginManager().callEvent(
                                    new MmoPlayerUseAbilityEvent(player, abilityArchetype, executor, event, trigger));
                        }
                    });
        });
    }

    static long remainingCooldown(long configuredCooldown, long elapsedCooldown) {
        return Math.max(1, configuredCooldown - elapsedCooldown);
    }

}
