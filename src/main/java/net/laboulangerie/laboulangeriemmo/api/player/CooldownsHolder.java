package net.laboulangerie.laboulangeriemmo.api.player;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;

public class CooldownsHolder {
    private HashMap<AbilityArchetype, Date> cooldowns;

    public CooldownsHolder() {
        cooldowns = new HashMap<AbilityArchetype, Date>();
    }

    public long getCooldown(AbilityArchetype ability) {
        return ability.cooldownUnit.convert(new Date().getTime() - cooldowns.get(ability).getTime(),
                TimeUnit.MILLISECONDS);
    }

    public long getCooldown(String abilityId) {
        return getCooldown(cooldowns.keySet().stream().filter(ability -> ability.identifier.equals(abilityId)).findFirst().orElse(null));
    }

    public boolean isCooldownElapsed(AbilityArchetype ability) {
        return cooldowns.get(ability) == null || getCooldown(ability) >= ability.cooldown;
    }

    public void startCooldown(AbilityArchetype ability) {
        cooldowns.put(ability, new Date());
    }

    public boolean hasUsed(AbilityArchetype ability) {
        return cooldowns.get(ability) != null;
    }
    public boolean hasUsed(String abilityId) {
        return cooldowns.keySet().stream().filter(ability -> ability.identifier.equals(abilityId)).findFirst().isPresent();
    }
}
