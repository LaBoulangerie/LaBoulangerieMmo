package net.laboulangerie.laboulangeriemmo.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.player.ability.Abilities;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class MmoPlayerUseAbilityEvent extends Event{

    private static final HandlerList handlers = new HandlerList();
    private MmoPlayer mmoPlayer;
    private Abilities ability;
    private AbilityTrigger trigger;
    private Event triggerEvent;

    public MmoPlayerUseAbilityEvent(MmoPlayer mmoPlayer, Abilities ability, Event triggerEvent) {
        this.mmoPlayer = mmoPlayer;
        this.ability = ability;
        this.trigger = ability.getExecutor().getAbilityTrigger();
        this.triggerEvent = triggerEvent;
    }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
    public MmoPlayer getMmoPlayer() { return mmoPlayer; }
    public AbilityTrigger getAbilityTrigger() { return trigger; }
    public Abilities getAbility() { return ability; }
    public Event getTriggerEvent() { return triggerEvent; }

}
