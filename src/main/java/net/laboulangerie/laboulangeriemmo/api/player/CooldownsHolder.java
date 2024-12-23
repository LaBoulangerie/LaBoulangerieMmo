package net.laboulangerie.laboulangeriemmo.api.player;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.core.json.GsonSerializable;

public class CooldownsHolder implements GsonSerializable {
    private HashMap<String, Date> cooldowns;

    public CooldownsHolder() {
        cooldowns = new HashMap<>();
    }

    public long getCooldown(AbilityArchetype ability, String talentId) {
        return ability.cooldownUnit.convert(
                new Date().getTime() - cooldowns.get(getId(ability, talentId)).getTime(),
                TimeUnit.MILLISECONDS);
    }

    public List<Long> getCooldowns(String abilityId) {
        return cooldowns.entrySet().stream()
                .filter(entry -> entry.getKey().endsWith("/" + abilityId)).map(entry -> {
                    AbilityArchetype archetype = LaBoulangerieMmo.talentsRegistry
                            .getTalent(entry.getKey().split("/")[0]).abilitiesArchetypes
                                    .get(entry.getKey().split("/")[1]);
                    return archetype.cooldownUnit.convert(
                            new Date().getTime() - entry.getValue().getTime(),
                            TimeUnit.MILLISECONDS);
                }).collect(Collectors.toList());
    }

    /**
     * Maps every talent that has this ability to the last time it was used for each of them
     * @param abilityId
     * @return key is the talent identifier associated to this instance of the ability and value is the time elapsed since it's last use
     */
    public HashMap<String, Long> getTimeSinceUsed(String abilityId) {
        return (HashMap<String, Long>) cooldowns.entrySet().stream()
                .filter(entry -> entry.getKey().endsWith("/" + abilityId))
                .collect(Collectors.toMap(e -> e.getKey().split("/")[0],  e -> new Date().getTime()-e.getValue().getTime()));
    }

    public boolean isCooldownElapsed(AbilityArchetype ability, String talentId) {
        return cooldowns.get(getId(ability, talentId)) == null
                || getCooldown(ability, talentId) >= ability.cooldown;
    }

    public void startCooldown(AbilityArchetype ability, String talentId) {
        cooldowns.put(getId(ability, talentId), new Date());
    }

    /**
     * Checks if the specified ability has been used in the specified talent
     * 
     * @param ability The ability to check
     * @param talentId The talent in which the ability must have been used
     */
    public boolean hasUsed(AbilityArchetype ability, String talentId) {
        return cooldowns.get(getId(ability, talentId)) != null;
    }

    /**
     * Checks if the specified ability has been used no matter the talent
     * 
     * @param abilityId The ability to check
     */
    public boolean hasUsed(String abilityId) {
        return cooldowns.keySet().stream().filter(key -> key.endsWith("/" + abilityId)).findFirst()
                .isPresent();
    }

    private String getId(AbilityArchetype ability, String talentId) {
        return talentId + "/" + ability.identifier;
    }

    public HashMap<String, Date> getCooldowns() {
        return cooldowns;
    }

    /**
     * Intended for internal use (data deserialisation)
     * 
     * Will set the cooldowns to match the given { @link HashMap } if the
     * internal one is empty. 
     * @param cooldowns
     */
    public void setCooldowns(HashMap<String, Date> cooldowns) {
        if (!this.cooldowns.isEmpty()) return;
        this.cooldowns = cooldowns;
    }
}
