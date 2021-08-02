package fr.laboulangerie.laboulangeriemmo.player.ability;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum Abilities {

    FAST_MINE(0, TimeUnit.MINUTES, 0, null, new FastMine());


    private int requiredLevel;
    private TimeUnit cooldownUnit;
    private int cooldown;

    private Class<?> parentTalentClass;
    private AbilityExecutor executor;

    Abilities(
        int requiredLevel, TimeUnit cooldownUnit,
        int cooldown, Class<?> parentTalentClass, 
        AbilityExecutor executor
    ) {
        this.requiredLevel = requiredLevel;
        this.cooldownUnit = cooldownUnit;
        this.parentTalentClass = parentTalentClass;
        this.executor = executor;
    }

    public AbilityExecutor getExecutor() {
        return this.executor;
    }

    public static Supplier<Stream<Abilities>> supplier() {
        return () -> Stream.of(Abilities.values());
    }

}
