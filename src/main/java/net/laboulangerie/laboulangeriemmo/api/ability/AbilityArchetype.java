package net.laboulangerie.laboulangeriemmo.api.ability;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AbilityArchetype {
    public String identifier;
    public String displayName;
    public boolean shouldLog;
    public TimeUnit cooldownUnit;
    public int cooldown;
    public int requiredLevel;
    /**
     * The id of the effect that should be played when
     * using this Ability, see {@link net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry EffectRegistry}
     */
    public String effect;
    /**
     * A list which contains the required level for each tier of the Ability.
     * This is optional.
     */
    public List<Integer> tiers = new ArrayList<>();

    /**
     * Get the level required for the specified tier or the
     * highest tier if it doesn't exist
     * @param tier you want to get the required level
     * @return Tier's required level or highest tier's one or 
     * requiredLevel if no tier is defined
     */
    public Integer getTier(int tier) {
        return tiers.size() > tier ? tiers.get(tier) : tiers.get(tiers.size()-1);
    }
}
