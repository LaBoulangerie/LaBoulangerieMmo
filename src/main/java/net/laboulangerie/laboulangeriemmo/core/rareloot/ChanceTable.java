package net.laboulangerie.laboulangeriemmo.core.rareloot;

import java.util.NavigableMap;
import java.util.TreeMap;

public final class ChanceTable {
    private final double defaultChance;
    private final NavigableMap<Integer, Double> levels;

    public ChanceTable(double defaultChance, NavigableMap<Integer, Double> levels) {
        validate(defaultChance);
        this.defaultChance = defaultChance;
        this.levels = new TreeMap<>(levels);
        this.levels.forEach((level, chance) -> {
            if (level < 0) throw new IllegalArgumentException("A chance level cannot be negative");
            validate(chance);
        });
    }

    public static ChanceTable fixed(double chance) {
        return new ChanceTable(chance, new TreeMap<>());
    }

    public double chanceAt(int level) {
        var threshold = levels.floorEntry(level);
        return threshold == null ? defaultChance : threshold.getValue();
    }

    private static void validate(double chance) {
        if (!Double.isFinite(chance) || chance < 0 || chance > 100) {
            throw new IllegalArgumentException("Chance must be between 0 and 100: " + chance);
        }
    }
}
