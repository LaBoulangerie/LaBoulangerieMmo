package net.laboulangerie.laboulangeriemmo.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class MmoListenerRewardTest {
    @Test
    void configuredJobsFollowProgressiveRewards() throws Exception {
        YamlConfiguration config = loadConfig();

        for (String job : new String[] {"miner", "hunter", "lumberjack", "farmer"}) {
            var rewards = config.getConfigurationSection("level-up-rewards." + job);
            assertEquals(10, MmoListener.calculateLevelReward(rewards, 1, 0));
            assertEquals(10, MmoListener.calculateLevelReward(rewards, 9, 0));
            assertEquals(20, MmoListener.calculateLevelReward(rewards, 10, 0));
            assertEquals(100, MmoListener.calculateLevelReward(rewards, 99, 0));
            assertEquals(110, MmoListener.calculateLevelReward(rewards, 100, 0));

            double total = 0;
            for (int level = 1; level <= 100; level++) {
                total += MmoListener.calculateLevelReward(rewards, level, 0);
            }
            assertEquals(5600, total);
        }
    }

    @Test
    void missingEmptyAndLegacyValuesHaveNoImplicitReward() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString("empty: {}\nlegacy-empty:\n  '*': ''\nlegacy:\n  '*': 2\n  '10': 3\n");

        assertEquals(0, MmoListener.calculateLevelReward(null, 10, 100));
        assertEquals(0, MmoListener.calculateLevelReward(config.getConfigurationSection("empty"), 10, 100));
        assertEquals(0, MmoListener.calculateLevelReward(
                config.getConfigurationSection("legacy-empty"), 10, 100));
        assertEquals(5, MmoListener.calculateLevelReward(config.getConfigurationSection("legacy"), 10, 100));
    }

    @Test
    void massiveXpGainPaysEveryCrossedLevel() throws Exception {
        YamlConfiguration config = loadConfig();
        var rewards = config.getConfigurationSection("level-up-rewards.hunter");

        assertEquals(320, MmoListener.calculateLevelRewards(rewards, 0, 20, "no", 0.1));
        assertEquals(30, MmoListener.calculateLevelRewards(rewards, 19, 20, "no", 0.1));
        assertEquals(0, MmoListener.calculateLevelRewards(rewards, 20, 20, "no", 0.1));
    }

    private YamlConfiguration loadConfig() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            YamlConfiguration config = new YamlConfiguration();
            config.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
            return config;
        }
    }
}
