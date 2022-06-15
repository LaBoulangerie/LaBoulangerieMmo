package net.laboulangerie.laboulangeriemmo.api.ability;

import org.bukkit.event.Event;

public abstract class AbilityExecutor {
    private AbilityArchetype archetype;

    public AbilityExecutor(AbilityArchetype archetype) {
        this.archetype = archetype;
    }

    /**
     * getAbilityTrigger indicate the type of interaction that should be done for a
     * given ability.
     * Example: CLICK_ENTITY, will trigger on PlayerInteractAtEntityEvent
     *
     * @return the type of trigger desired
     */
    public abstract AbilityTrigger getAbilityTrigger();

    /**
     * shouldTrigger method indicate if the ability should trigger or not based on
     * the event where it happen.
     * ps: the cooldown will be managed elsewhere
     *
     * @param baseEvent can be any instance of Event
     * @return True if the ability can trigger,false if not
     */
    public abstract boolean shouldTrigger(Event baseEvent);

    /**
     * trigger method indicate the core of an ability
     *
     * @param baseEvent can be any instance of Event
     * @param level     level of the player in the concerned talent
     */
    public abstract void trigger(Event baseEvent, int level);

    /**
     * @return The {@link net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype AbilityArchetype}
     * used by this executor
     */
    public AbilityArchetype getArchetype() {
        return archetype;
    }
    /**
     * Get the level corresponding to the tier specified
     * @param tier
     * @return first level of the tier
     */
    public Integer getTier(int tier) {
        return archetype.getTier(tier);
    }
}
