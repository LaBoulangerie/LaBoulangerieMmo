package fr.laboulangerie.laboulangeriemmo.player;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import fr.laboulangerie.laboulangeriemmo.player.ability.Abilities;

public class CooldownsHolder {
    private HashMap<Abilities, Date> cooldowns;

    public CooldownsHolder() {
        cooldowns = new HashMap<Abilities, Date>();
    }

    public long getCooldown(Abilities ability) {
        return ability.getCooldownUnit().convert(new Date().getTime() - cooldowns.get(ability).getTime(), 
            TimeUnit.MILLISECONDS);
    }

    public boolean isCooldownElapsed(Abilities ability) {
        return cooldowns.get(ability) == null || getCooldown(ability) >= ability.getCooldown();
    }

    public void startCooldown(Abilities ability) {
        cooldowns.put(ability, new Date());
    }
}
