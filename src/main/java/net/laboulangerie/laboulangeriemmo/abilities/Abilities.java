package net.laboulangerie.laboulangeriemmo.abilities;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.laboulangerie.laboulangeriemmo.abilities.farmer.AnimalTwins;
import net.laboulangerie.laboulangeriemmo.abilities.farmer.BetterBonemeal;
import net.laboulangerie.laboulangeriemmo.abilities.farmer.NatureTouch;
import net.laboulangerie.laboulangeriemmo.abilities.farmer.TastyBread;
import net.laboulangerie.laboulangeriemmo.abilities.mining.FastMine;
import net.laboulangerie.laboulangeriemmo.abilities.mining.FastSmelt;
import net.laboulangerie.laboulangeriemmo.abilities.mining.MagneticField;
import net.laboulangerie.laboulangeriemmo.abilities.mining.MinecraftExpMultiplier;
import net.laboulangerie.laboulangeriemmo.abilities.thehunter.Dodging;
import net.laboulangerie.laboulangeriemmo.abilities.thehunter.ExpInBottle;
import net.laboulangerie.laboulangeriemmo.abilities.thehunter.FireBow;
import net.laboulangerie.laboulangeriemmo.abilities.thehunter.Hiding;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.BetterAppleDrop;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.DoubleDropLog;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.Strip;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.ThickTree;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.Timber;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public enum Abilities {

    MINECRAFT_EXP_MULTIPLIER(5, TimeUnit.SECONDS, 1, "mining", new MinecraftExpMultiplier(), false, "default"),
    FAST_MINE(15, TimeUnit.MINUTES, 15, "mining", new FastMine(), false, "default"),
    FAST_SMELT(85, TimeUnit.HOURS, 1, "mining", new FastSmelt(), true, "default"),
    MAGNETIC_FIELD(35, TimeUnit.MINUTES, 30, "mining", new MagneticField(), true, "default"),

    DOUBLE_DROP_LOG(0, TimeUnit.SECONDS, 1, "woodcutting", new DoubleDropLog(), true, "default"),
    BETTER_APPLE_DROP(1, TimeUnit.SECONDS, 1, "woodcutting", new BetterAppleDrop(), false, "default"),
    TIMBER(0, TimeUnit.SECONDS, 1, "woodcutting", new Timber(), true, "default"),
    STRIP(0, TimeUnit.SECONDS, 1, "woodcutting", new Strip(), true, "default"),
    THICK_TREE(0, TimeUnit.SECONDS, 0, "woodcutting", new ThickTree(), true, "default"),

    EXP_IN_BOTTLE(0, TimeUnit.SECONDS, 1, "thehunter", new ExpInBottle(), false, "default"),
    DODGING(0, TimeUnit.SECONDS, 0, "thehunter", new Dodging(), true, "trail"),
    FIRE_BOW(0, TimeUnit.SECONDS, 0, "thehunter", new FireBow(), true, "default"),
    HIDING(1, TimeUnit.SECONDS, 0, "thehunter", new Hiding(), true, "default"),

    NATURE_TOUCH(1, TimeUnit.SECONDS, 0, "farmer", new NatureTouch(), false, "default"),
    BETTER_BONEMEAL(1, TimeUnit.SECONDS, 0, "farmer", new BetterBonemeal(), true, "default"),
    ANIMAL_TWINS(1, TimeUnit.SECONDS, 0, "farmer", new AnimalTwins(), false, "default"),
    TASTY_BREAD(0, TimeUnit.SECONDS, 0, "farmer", new TastyBread(), false, "default");

    private int requiredLevel;
    private TimeUnit cooldownUnit;
    private int cooldown;
    private String parentTalent;
    private AbilityExecutor executor;
    /**
     * if it is set to false no message will be sent and no particle effect will be
     * played
     */
    private Boolean shouldlog;
    private String effectName;

    Abilities(
            int requiredLevel, TimeUnit cooldownUnit,
            int cooldown, String parentTalent,
            AbilityExecutor executor,
            boolean shouldlog,
            String effectName) {
        this.requiredLevel = requiredLevel;
        this.cooldownUnit = cooldownUnit;
        this.cooldown = cooldown;
        this.parentTalent = parentTalent;
        this.executor = executor;
        this.shouldlog = shouldlog;
        this.effectName = effectName;
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

    public String getEffectName() {
        return effectName;
    }

    public static Supplier<Stream<Abilities>> supplier() {
        return () -> Stream.of(Abilities.values());
    }
}
