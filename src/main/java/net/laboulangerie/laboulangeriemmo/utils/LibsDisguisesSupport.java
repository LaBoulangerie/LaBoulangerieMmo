package net.laboulangerie.laboulangeriemmo.utils;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;

import org.bukkit.entity.Entity;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.MobVisualProfile;

/** Reads the disguise that is actually active, including changes made by skills. */
public final class LibsDisguisesSupport {
    private LibsDisguisesSupport() {}

    public static Optional<MobVisualProfile> getVisualProfile(Entity entity) {
        if (!DisguiseAPI.isDisguised(entity)) return Optional.empty();
        Disguise disguise = DisguiseAPI.getDisguise(entity);
        if (disguise == null || !disguise.isMobDisguise()) return Optional.empty();

        String type = disguise.getType().name();
        type = switch (type) {
            case "MUSHROOM_COW" -> "MOOSHROOM";
            case "SNOWMAN" -> "SNOW_GOLEM";
            default -> type;
        };

        Object watcher = invoke(disguise, "getWatcher");
        String condition = watcher == null ? null : condition(type, watcher);
        return Optional.of(new MobVisualProfile(type, condition));
    }

    private static String condition(String type, Object watcher) {
        if ("WOLF".equals(type)) {
            String variant = enumName(watcher, "getVariant");
            if (variant == null) return isBaby(watcher) ? "BABY" : null;
            if (isBaby(watcher)) return "BABY_" + variant;
            boolean angry = bool(watcher, false, "isAngry", "isAggressive")
                    || positive(watcher, "getAnger");
            return angry ? "ANGRY_" + variant : variant;
        }

        String base = switch (type) {
            case "PANDA" -> pandaCondition(watcher);
            case "ARMADILLO" -> enumName(watcher, "getState");
            case "ZOMBIE_NAUTILUS" -> enumName(watcher, "getVariant");
            case "TRADER_LLAMA", "SHEEP", "HORSE", "LLAMA" -> enumName(watcher, "getColor");
            case "SNOW_GOLEM" -> bool(watcher, false, "isDerp") ? "NO_PUMPKIN" : "PUMPKIN";
            case "COPPER_GOLEM" -> firstEnumName(watcher, "getWeatheringState", "getState");
            case "PIG", "CHICKEN", "COW", "MOOSHROOM", "FROG", "AXOLOTL", "PARROT" ->
                enumName(watcher, "getVariant");
            case "VEX" -> bool(watcher, false, "isCharging", "isAngry") ? "ANGRY" : "NORMAL";
            case "CAT" -> firstEnumName(watcher, "getCatType", "getType", "getVariant");
            case "STRIDER" -> bool(watcher, false, "isShivering") ? "COLD" : null;
            case "GOAT" -> bool(watcher, false, "isScreaming") ? "SCREAMING" : null;
            case "BEE" -> beeCondition(watcher);
            case "FOX" -> {
                String foxType = firstEnumName(watcher, "getFoxType", "getType", "getVariant");
                yield "RED".equals(foxType) ? null : foxType;
            }
            case "RABBIT" -> firstEnumName(watcher, "getRabbitType", "getType", "getVariant");
            default -> null;
        };

        if (supportsBabyCondition(type) && isBaby(watcher)) return base == null ? "BABY" : "BABY_" + base;
        return base;
    }

    private static boolean supportsBabyCondition(String type) {
        return switch (type) {
            case "PANDA", "SHEEP", "PIG", "CHICKEN", "COW", "MOOSHROOM", "CAT", "STRIDER",
                    "GOAT", "BEE", "AXOLOTL", "FOX", "RABBIT", "HORSE", "LLAMA" -> true;
            default -> false;
        };
    }

    private static String pandaCondition(Object watcher) {
        String main = firstEnumName(watcher, "getMainGene", "getGene");
        String hidden = enumName(watcher, "getHiddenGene");
        if (("BROWN".equals(main) || "WEAK".equals(main)) && !main.equals(hidden)) main = "NORMAL";
        return main;
    }

    private static String beeCondition(Object watcher) {
        boolean nectar = bool(watcher, false, "hasNectar", "isNectar");
        boolean angry = bool(watcher, false, "isAngry", "isAggressive") || positive(watcher, "getBeeAnger", "getAnger");
        if (nectar && angry) return "POLLINATED_ANGRY";
        if (nectar) return "POLLINATED";
        return angry ? "ANGRY" : null;
    }

    private static boolean positive(Object target, String... methods) {
        for (String method : methods) {
            Object value = invoke(target, method);
            if (value instanceof Number number) return number.doubleValue() > 0;
        }
        return false;
    }

    private static boolean isBaby(Object watcher) {
        Object adult = invoke(watcher, "isAdult");
        if (adult instanceof Boolean value) return !value;
        return bool(watcher, false, "isBaby");
    }

    private static String firstEnumName(Object target, String... methods) {
        for (String method : methods) {
            String value = enumName(target, method);
            if (value != null) return value;
        }
        return null;
    }

    private static String enumName(Object target, String method) {
        Object value = invoke(target, method);
        if (value == null) return null;
        if (value instanceof Enum<?> enumValue) return enumValue.name();
        Object key = invoke(value, "getKey");
        if (key != null && key != value) {
            Object keyName = invoke(key, "getKey");
            if (keyName != null) return keyName.toString().toUpperCase(Locale.ROOT);
        }
        return value.toString().toUpperCase(Locale.ROOT);
    }

    private static boolean bool(Object target, boolean fallback, String... methods) {
        for (String method : methods) {
            Object value = invoke(target, method);
            if (value instanceof Boolean bool) return bool;
        }
        return fallback;
    }

    private static Object invoke(Object target, String methodName) {
        if (target == null) return null;
        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }
}
