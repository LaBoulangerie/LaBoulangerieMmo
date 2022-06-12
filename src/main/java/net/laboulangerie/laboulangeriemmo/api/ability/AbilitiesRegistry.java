package net.laboulangerie.laboulangeriemmo.api.ability;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class AbilitiesRegistry {
    private Map<String, Class<? extends AbilityExecutor>> abilities = new HashMap<>();
    private Map<String, AbilityTrigger> triggerIndex = new HashMap<>();

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
    public void registerAbility(String abilityId, Class<? extends AbilityExecutor> abilityClass, AbilityTrigger trigger) {
        if (validateAbility(abilityId)) {
            abilities.put(abilityId, abilityClass);
            triggerIndex.put(abilityId, trigger);
        }
    }
    public boolean exists(String abilityId) {
        return abilities.containsKey(abilityId);
    }
    private boolean validateAbility(String abilityId) {
        if (exists(abilityId)) {
            LaBoulangerieMmo.PLUGIN.getLogger().warning("Can't register ability '"+ abilityId +"', it already exists!");
            return false;
        }
        return true;
    }
    public AbilityTrigger getTriggerForAbility(String abilityId) {
        return triggerIndex.get(abilityId);
    }
    /**
     * Return a fresh instance of the {@link net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor AbilityExecutor}
     * corresponding to the provided {@link net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype AbilityArchetype}
     * @param abilityArchetype
     * @return A new instance or an error if the instantiation failed
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public AbilityExecutor newAbilityExecutor(AbilityArchetype abilityArchetype) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Class<? extends AbilityExecutor> executorClass = abilities.get(abilityArchetype.identifier);
        if (executorClass == null) {
            throw new IllegalArgumentException("No ability named '"+ abilityArchetype.identifier +"'!");
        }
        return executorClass.getConstructor(AbilityArchetype.class).newInstance(abilityArchetype);
    }
}
