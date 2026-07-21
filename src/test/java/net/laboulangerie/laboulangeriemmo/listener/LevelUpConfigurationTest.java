package net.laboulangerie.laboulangeriemmo.listener;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class LevelUpConfigurationTest {
    @Test
    void levelUpsHaveNoMoneyRewardConfigurationOrVaultDependency() throws Exception {
        YamlConfiguration config = loadYaml("config.yml");
        YamlConfiguration plugin = loadYaml("plugin.yml");

        assertNotNull(config.getString("lang.messages.level-up"));
        assertFalse(config.getString("lang.messages.level-up").contains("<reward>"));
        assertFalse(config.contains("lang.messages.level-up-no-reward"));
        assertFalse(config.contains("level-up-rewards"));
        assertFalse(config.contains("rewards-rounding-method"));
        assertFalse(plugin.getStringList("depend").contains("Vault"));
        assertFalse(plugin.getStringList("softdepend").contains("Vault"));
    }

    private YamlConfiguration loadYaml(String resource) throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(stream);
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
            return yaml;
        }
    }
}
