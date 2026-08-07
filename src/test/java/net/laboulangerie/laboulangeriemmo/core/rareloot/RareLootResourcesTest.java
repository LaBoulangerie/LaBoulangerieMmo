package net.laboulangerie.laboulangeriemmo.core.rareloot;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class RareLootResourcesTest {
    private static final Map<String, JobPalette> JOB_PALETTES = Map.of(
            "Mineur", new JobPalette("<#70B7E6>", "<#92A9B8>", Set.of(
                    "geode_shard", "luminous_core", "perfect_gem", "primordial_slag")),
            "Bûcheron", new JobPalette("<#D99A6C>", "<#B0A097>", Set.of(
                    "amber_sap", "ancient_knot", "spectral_bark", "sylvan_heart")),
            "Fermier", new JobPalette("<#82C982>", "<#9EADA0>", Set.of(
                    "solar_grain", "royal_tuber", "aether_spore", "fertility_essence")),
            "Chasseur", new JobPalette("<#D9828B>", "<#AD9A9D>", Set.of(
                    "reinforced_bone", "toxic_gland", "spectral_eye", "monstrous_heart")));

    private static final String[] RESOURCES = {
        "rare-loots/settings.yml",
        "rare-loots/items.yml",
        "rare-loots/chances.yml",
        "rare-loots/jobs/hunter.yml",
        "rare-loots/jobs/lumberjack.yml",
        "rare-loots/jobs/farmer.yml",
        "rare-loots/jobs/miner.yml"
    };

    @Test
    void masteryLootMessageIsConfiguredAndValidMiniMessage() throws Exception {
        YamlConfiguration config = load("config.yml");
        String message = config.getString("lang.messages.mastery-loot");
        assertNotNull(message);
        MiniMessage.miniMessage().deserialize(message);
    }

    @Test
    void abilityCooldownMessageIsConfiguredAndValidMiniMessage() throws Exception {
        YamlConfiguration config = load("config.yml");
        String message = config.getString("lang.messages.ability-use-cooldown");
        assertNotNull(message);
        MiniMessage.miniMessage().deserialize(message);
    }

    @Test
    void bundledConfigurationsAreValidYaml() throws Exception {
        for (String resource : RESOURCES) {
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource)) {
                assertNotNull(stream, "Missing resource " + resource);
                String yaml = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                new YamlConfiguration().loadFromString(yaml);
            }
        }
    }

    @Test
    void catalogueContainsFourValidActiveItemsPerJob() throws Exception {
        YamlConfiguration itemsConfig = load("rare-loots/items.yml");
        ConfigurationSection items = itemsConfig.getConfigurationSection("items");
        assertNotNull(items);
        assertEquals(17, items.getKeys(false).size());

        Set<Integer> models = new HashSet<>();
        for (String itemId : items.getKeys(false)) {
            assertEquals("native", items.getString(itemId + ".provider"));
            if (items.contains(itemId + ".custom-model-data")) {
                assertTrue(models.add(items.getInt(itemId + ".custom-model-data")),
                        "Duplicate CustomModelData for " + itemId);
            }
        }
        assertEquals(16, models.size());
        assertEquals("VEX_ARMOR_TRIM_SMITHING_TEMPLATE", items.getString("vex_armor_trim.material"));
        assertTrue(!items.contains("vex_armor_trim.name"));
        assertTrue(!items.contains("vex_armor_trim.lore"));

        Set<Integer> initialLevels = new HashSet<>();
        for (String job : new String[] {"hunter", "lumberjack", "farmer", "miner"}) {
            YamlConfiguration jobConfig = load("rare-loots/jobs/" + job + ".yml");
            assertEquals(job, jobConfig.getString("job"));
            ConfigurationSection rules = jobConfig.getConfigurationSection("rules");
            assertNotNull(rules);
            assertEquals(job.equals("hunter") ? 5 : 4, rules.getKeys(false).size(), job);
            for (String ruleId : rules.getKeys(false)) {
                String path = "rules." + ruleId;
                assertTrue(rules.getBoolean(ruleId + ".enabled"), job + ':' + ruleId);
                assertTrue(items.contains(rules.getString(ruleId + ".loot.item")), job + ':' + ruleId);
                assertEquals(0, rules.getDouble(ruleId + ".loot.chance.default"));
                ConfigurationSection levels = rules.getConfigurationSection(ruleId + ".loot.chance.levels");
                assertNotNull(levels, job + ':' + ruleId);
                int initialLevel = levels.getKeys(false).stream().mapToInt(Integer::parseInt).min().orElseThrow();
                assertTrue(initialLevels.add(initialLevel), "Repeated initial level " + initialLevel);
            }
        }
        assertEquals(17, initialLevels.size());

        YamlConfiguration hunter = load("rare-loots/jobs/hunter.yml");
        assertEquals(Set.of("PILLAGER", "VINDICATOR", "EVOKER", "ILLUSIONER"),
                Set.copyOf(hunter.getStringList("rules.vex_armor_trim.conditions.entity-types")));
        ChanceTable vexTrimChance = new ChanceTable(
                hunter.getDouble("rules.vex_armor_trim.loot.chance.default"),
                new java.util.TreeMap<>(java.util.Map.of(
                        100, hunter.getDouble("rules.vex_armor_trim.loot.chance.levels.100"))));
        assertEquals(0, vexTrimChance.chanceAt(99));
        assertEquals(0.10, vexTrimChance.chanceAt(100));
    }

    @Test
    void masteryItemsUseTheirJobPaletteAndValidProvenance() throws Exception {
        ConfigurationSection items = load("rare-loots/items.yml").getConfigurationSection("items");
        assertNotNull(items);

        for (var entry : JOB_PALETTES.entrySet()) {
            String job = entry.getKey();
            JobPalette palette = entry.getValue();
            for (String itemId : palette.itemIds()) {
                String name = items.getString(itemId + ".name");
                List<String> lore = items.getStringList(itemId + ".lore");

                assertNotNull(name, itemId);
                assertTrue(name.startsWith(palette.nameColor()), itemId + " name");
                assertEquals(2, lore.size(), itemId + " lore");
                assertTrue(lore.get(0).startsWith(palette.descriptionColor()), itemId + " description");
                assertTrue(lore.get(1).endsWith("Butin de maîtrise du " + job), itemId + " provenance");

                MiniMessage.miniMessage().deserialize(name);
                lore.forEach(line -> MiniMessage.miniMessage().deserialize(line));
            }
        }
    }

    private YamlConfiguration load(String resource) throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(stream, "Missing resource " + resource);
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.loadFromString(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
            return configuration;
        }
    }

    private record JobPalette(String nameColor, String descriptionColor, Set<String> itemIds) {}
}
