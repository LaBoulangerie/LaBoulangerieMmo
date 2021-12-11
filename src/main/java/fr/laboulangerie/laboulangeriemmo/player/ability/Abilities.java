package fr.laboulangerie.laboulangeriemmo.player.ability;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import fr.laboulangerie.laboulangeriemmo.player.ability.mining.FastMine;
import fr.laboulangerie.laboulangeriemmo.player.ability.mining.FastSmelt;
import fr.laboulangerie.laboulangeriemmo.player.ability.mining.MinecraftExpMultiplier;
import fr.laboulangerie.laboulangeriemmo.player.ability.thehunter.ExpInBottle;
import fr.laboulangerie.laboulangeriemmo.player.ability.mining.MagneticField;
import fr.laboulangerie.laboulangeriemmo.player.ability.woodcutting.BetterAppleDrop;
import fr.laboulangerie.laboulangeriemmo.player.ability.woodcutting.DoubleDropLog;

public enum Abilities {


    MINECRAFT_EXP_MULTIPLIER(5, TimeUnit.SECONDS, 1, "mining", new MinecraftExpMultiplier(), false),
    FAST_MINE(15, TimeUnit.MINUTES, 15, "mining", new FastMine(), false),
    FAST_SMELT(85, TimeUnit.HOURS, 1, "mining", new FastSmelt(), true),
    MAGNETIC_FIELD(35, TimeUnit.MINUTES, 30, "mining", new MagneticField(), true),
    DOUBLE_DROP_LOG(1, TimeUnit.SECONDS, 1, "woodcutting", new DoubleDropLog(), false),
	BETTER_APPLE_DROP(1, TimeUnit.SECONDS, 1, "woodcutting", new BetterAppleDrop(), false),
	EXP_IN_BOTTLE(1, TimeUnit.SECONDS, 1, "thehunter", new ExpInBottle(), false);

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
