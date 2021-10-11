package fr.laboulangerie.laboulangeriemmo.player.ability;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import fr.laboulangerie.laboulangeriemmo.player.ability.mining.FastMine;
import fr.laboulangerie.laboulangeriemmo.player.ability.mining.FastSmelt;
import fr.laboulangerie.laboulangeriemmo.player.ability.mining.MagneticField;

public enum Abilities {

    FAST_MINE(10, TimeUnit.SECONDS, 25, "mining", new FastMine()),
    FAST_SMELT(1, TimeUnit.SECONDS, 10, "mining", new FastSmelt()),
    MAGNETIC_FIELD(0, TimeUnit.SECONDS, 5, "mining", new MagneticField());


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
        this.cooldown = cooldown;
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
