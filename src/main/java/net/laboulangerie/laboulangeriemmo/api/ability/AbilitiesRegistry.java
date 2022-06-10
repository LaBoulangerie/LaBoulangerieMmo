package net.laboulangerie.laboulangeriemmo.api.ability;

import java.util.HashMap;
import java.util.Map;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class AbilitiesRegistry {
    Map<String, Class<? extends AbilityExecutor>> abilities = new HashMap<>();

    public Class<? extends AbilityExecutor> getAbility(String abilityId) {
        return abilities.get(abilityId);
    }
    /**
     * Add an ability to the AbilitiesRegistry
     * @param abilityId Unique identifier of the ability
     * @param abilityClass A class extending {@link net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor AbilityExecutor}
     * @return false if the {@link net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype AbilityArchetype}
     * is invalid (e.g: ability already exists).
     * Returns true otherwise
     */
    public void addAbility(String abilityId, Class<? extends AbilityExecutor> abilityClass) {
        if (validateAbility(abilityId)) {
            abilities.put(abilityId, abilityClass);
        }
    }
    public boolean exists(String abilityId) {
        return abilities.containsKey(abilityId);
    }
    private boolean validateAbility(String abilityId) {
        if (exists(abilityId)) {
            LaBoulangerieMmo.PLUGIN.getLogger().warning("Can't register ability '"+ abilityId +"', it already exists!");
            return true;
        }
        return false;
    }
}
