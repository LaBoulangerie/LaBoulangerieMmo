package net.laboulangerie.laboulangeriemmo.utils;

import java.util.Locale;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public final class MythicMobPolicy {
    public enum HeadMode { NONE, BASE, DISGUISE }

    private MythicMobPolicy() {}

    public static HeadMode headMode(String internalName) {
        FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();
        Object override = findObject(config, "mythicmobs.heads.overrides", internalName);
        Object configured = override != null ? override : config.get("mythicmobs.heads.default", "NONE");
        String raw = configured instanceof String string ? string : String.valueOf(configured);
        try {
            return HeadMode.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (RuntimeException exception) {
            warn("Invalid MythicMobs head mode '" + raw + "' for " + internalName + "; using NONE");
            return HeadMode.NONE;
        }
    }

    /** Returns null for AUTO, otherwise an explicit non-negative XP amount. */
    public static Double hunterXp(String internalName) {
        FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();
        Object value = findObject(config, "mythicmobs.hunter-xp.overrides", internalName);
        if (value == null) value = config.get("mythicmobs.hunter-xp.default", "AUTO");
        if (value instanceof String string && string.trim().equalsIgnoreCase("AUTO")) return null;
        if (value instanceof Number number && Double.isFinite(number.doubleValue()) && number.doubleValue() >= 0)
            return number.doubleValue();
        warn("Invalid MythicMobs hunter XP value '" + value + "' for " + internalName + "; using AUTO");
        return null;
    }

    private static Object findObject(FileConfiguration config, String path, String key) {
        var section = config.getConfigurationSection(path);
        if (section == null) return null;
        for (String candidate : section.getKeys(false)) {
            if (candidate.equalsIgnoreCase(key)) return section.get(candidate);
        }
        return null;
    }

    private static void warn(String message) {
        Logger logger = LaBoulangerieMmo.PLUGIN.getLogger();
        logger.warning(message);
    }
}
