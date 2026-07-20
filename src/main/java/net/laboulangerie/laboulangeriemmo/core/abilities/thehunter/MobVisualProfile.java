package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter;

import java.util.Locale;

/** The mob-head lookup key independently of the source (Bukkit entity or disguise). */
public record MobVisualProfile(String mobType, String condition) {
    public MobVisualProfile {
        if (mobType == null || mobType.isBlank()) throw new IllegalArgumentException("mobType cannot be blank");
        mobType = mobType.toUpperCase(Locale.ROOT);
        condition = condition == null || condition.isBlank() ? null : condition.toUpperCase(Locale.ROOT);
    }
}
