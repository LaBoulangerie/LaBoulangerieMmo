package fr.laboulangerie.laboulangeriemmo.player.ability;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum Abilities {

    FAST_MINE(5, TimeUnit.MINUTES, 0, "mining", new FastMine());


    private int requiredLevel;
    private TimeUnit cooldownUnit;
    private int cooldown;

    private String parentTalent;
    private AbilityExecutor executor;

    Abilities(
        int requiredLevel, TimeUnit cooldownUnit,
        int cooldown, String parentTalent, 
        AbilityExecutor executor
    ) {
        this.requiredLevel = requiredLevel;
        this.cooldownUnit = cooldownUnit;
        this.parentTalent = parentTalent;
        this.executor = executor;
    }

    public AbilityExecutor getExecutor() {
        return this.executor;
    }

    public String getParentTalent() {
        return parentTalent;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getCooldown() {
        return cooldown;
    }

    public TimeUnit getCooldownUnit() {
        return cooldownUnit;
    }

    public static Supplier<Stream<Abilities>> supplier() {
        return () -> Stream.of(Abilities.values());
    }

}
