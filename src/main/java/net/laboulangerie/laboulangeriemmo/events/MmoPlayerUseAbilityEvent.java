package net.laboulangerie.laboulangeriemmo.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityTrigger;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;

/**
 * Called just after a player used an ability
 */
public class MmoPlayerUseAbilityEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private MmoPlayer mmoPlayer;
    private AbilityExecutor executor;
    private AbilityArchetype ability;
    private AbilityTrigger trigger;
    private Event triggerEvent;

    public MmoPlayerUseAbilityEvent(MmoPlayer mmoPlayer, AbilityArchetype ability,
            AbilityExecutor executor, Event triggerEvent, AbilityTrigger trigger) {
        this.mmoPlayer = mmoPlayer;
        this.ability = ability;
        this.executor = executor;
        this.trigger = trigger;
        this.triggerEvent = triggerEvent;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MmoPlayer getMmoPlayer() {
        return mmoPlayer;
    }

    public AbilityTrigger getAbilityTrigger() {
        return trigger;
    }

    public AbilityArchetype getAbility() {
        return ability;
    }

    public AbilityExecutor getExecutor() {
        return executor;
    }

    /**
     * The event that triggered the ability can be cast according to the {@code AbilityTrigger} used
     * 
     * @return the event that triggered the ability
     */
    public Event getTriggerEvent() {
        return triggerEvent;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(mmoPlayer.getUniqueId());
    }
}
