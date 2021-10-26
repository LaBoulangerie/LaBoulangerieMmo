package fr.laboulangerie.laboulangeriemmo.player.ability;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import fr.laboulangerie.laboulangeriemmo.player.ability.mining.FastMine;
import fr.laboulangerie.laboulangeriemmo.player.ability.mining.FastSmelt;
import fr.laboulangerie.laboulangeriemmo.player.ability.mining.MinecraftExpMultiplier;
import fr.laboulangerie.laboulangeriemmo.player.ability.mining.MagneticField;
import fr.laboulangerie.laboulangeriemmo.player.ability.woodcutting.BetterAppleDrop;
import fr.laboulangerie.laboulangeriemmo.player.ability.woodcutting.DoubleDropLog;

public enum Abilities {

    FAST_MINE(10, TimeUnit.SECONDS, 25, "mining", new FastMine(), true),
    FAST_SMELT(1, TimeUnit.SECONDS, 10, "mining", new FastSmelt(), true),
    MAGNETIC_FIELD(0, TimeUnit.SECONDS, 5, "mining", new MagneticField(), true),
    DOUBLE_DROP_LOG(1, TimeUnit.SECONDS, 1, "woodcutting", new DoubleDropLog(), false),
	BETTER_APPLE_DROP(1, TimeUnit.SECONDS, 1, "woodcutting", new BetterAppleDrop(), false),
    MINECRAFT_EXP_MULTIPLIER(1, TimeUnit.SECONDS, 1, "mining", new MinecraftExpMultiplier(), false);

    private int requiredLevel;
    private TimeUnit cooldownUnit;
    private int cooldown;

    private String parentTalent;
    private AbilityExecutor executor;
	private Boolean shouldlog;

    Abilities(
        int requiredLevel, TimeUnit cooldownUnit,
        int cooldown, String parentTalent,
        AbilityExecutor executor,
        boolean shouldlog
    ) {
        this.requiredLevel = requiredLevel;
        this.cooldownUnit = cooldownUnit;
        this.cooldown = cooldown;
        this.parentTalent = parentTalent;
        this.executor = executor;
        this.shouldlog = shouldlog;
        
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
    public boolean shouldLog() {
    	return shouldlog;
    }

    public static Supplier<Stream<Abilities>> supplier() {
        return () -> Stream.of(Abilities.values());
    }

}
