package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import org.bukkit.event.Event;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class NatureShelterAbility extends AbilityExecutor {

    public NatureShelterAbility(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        return false; // Handled by background task
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        // No-op - handled by NatureShelter.startTask()
    }
}
