package net.laboulangerie.laboulangeriemmo.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class GrindingConfigurationTest {
    @Test
    void configContainsTheNewHunterAndFarmerRewards() throws Exception {
        YamlConfiguration config = loadConfig();

        assertValues(config, "talent-grinding.hunter.kill", Map.of(
                "BOGGED", 7.0,
                "BREEZE", 12.0,
                "PARCHED", 8.0,
                "ZOMBIE_NAUTILUS", 10.0));
        assertFalse(config.contains("talent-grinding.hunter.kill.CAMEL_HUSK"));
        assertFalse(config.contains("talent-grinding.hunter.kill.ZOMBIE_HORSE"));
        assertFalse(config.contains("talent-grinding.hunter.kill.CREAKING"));

        assertValues(config, "talent-grinding.farmer.breed", Map.of(
                "MULE", 3.0,
                "CAMEL", 3.0,
                "SNIFFER", 5.0,
                "ARMADILLO", 2.0,
                "NAUTILUS", 4.0));
    }

    @Test
    void configCompletesEveryUnstrippedWoodFamily() throws Exception {
        YamlConfiguration config = loadConfig();
        String path = "talent-grinding.lumberjack.break.";

        assertPairs(config, path, "OAK", "SPRUCE", "BIRCH", "JUNGLE", "ACACIA", "DARK_OAK", "MANGROVE",
                "CHERRY", "PALE_OAK");
        assertEquals(2.0, config.getDouble(path + "CRIMSON_STEM"));
        assertEquals(2.0, config.getDouble(path + "CRIMSON_HYPHAE"));
        assertEquals(2.0, config.getDouble(path + "WARPED_STEM"));
        assertEquals(2.0, config.getDouble(path + "WARPED_HYPHAE"));
        assertFalse(config.contains(path + "CREAKING_HEART"));
        assertFalse(config.contains(path + "POINTED_DRIPSTONE"));
    }

    @Test
    void trialMultipliersAreConfigurable() throws Exception {
        YamlConfiguration config = loadConfig();

        assertEquals(1.5, GrindingListener.configuredTrialMultiplier(config, false));
        assertEquals(2.0, GrindingListener.configuredTrialMultiplier(config, true));
    }

    private void assertPairs(YamlConfiguration config, String path, String... families) {
        for (String family : families) {
            double logXp = config.getDouble(path + family + "_LOG");
            assertEquals(logXp, config.getDouble(path + family + "_WOOD"), family);
        }
    }

    private void assertValues(YamlConfiguration config, String path, Map<String, Double> values) {
        values.forEach((key, value) -> assertEquals(value, config.getDouble(path + "." + key), key));
    }

    private YamlConfiguration loadConfig() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            YamlConfiguration config = new YamlConfiguration();
            config.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
            return config;
        }
    }
}
