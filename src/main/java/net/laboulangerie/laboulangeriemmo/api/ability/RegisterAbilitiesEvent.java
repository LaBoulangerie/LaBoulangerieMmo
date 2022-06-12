package net.laboulangerie.laboulangeriemmo.api.ability;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when the {@link net.laboulangerie.laboulangeriemmo.api.ability.AbilitiesRegistry AbilitiesRegistry} is 
 * ready to accept registering {@link net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor abilities}
 */
public class RegisterAbilitiesEvent extends Event {
    private static HandlerList HANDLERS = new HandlerList();
    private AbilitiesRegistry registry;

    public RegisterAbilitiesEvent(AbilitiesRegistry registry) {
        this.registry = registry;
    }
    /**
     * Get the {@link net.laboulangerie.laboulangeriemmo.api.ability.AbilitiesRegistry AbilitiesRegistry}
     * against which you can register your abilities
     * @return
     */
    public AbilitiesRegistry getRegistry() { return registry; }
    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
