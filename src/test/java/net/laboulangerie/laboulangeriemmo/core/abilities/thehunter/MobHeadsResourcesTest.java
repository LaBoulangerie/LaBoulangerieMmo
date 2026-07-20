package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class MobHeadsResourcesTest {
    private static final Set<String> BABY_WOLF_CONDITIONS = Set.of(
            "BABY_PALE", "BABY_WOODS", "BABY_ASHEN", "BABY_BLACK", "BABY_CHESTNUT",
            "BABY_RUSTY", "BABY_SPOTTED", "BABY_STRIPED", "BABY_SNOWY");
    private static final Set<String> ANGRY_WOLF_CONDITIONS = Set.of(
            "ANGRY_PALE", "ANGRY_WOODS", "ANGRY_ASHEN", "ANGRY_BLACK", "ANGRY_CHESTNUT",
            "ANGRY_RUSTY", "ANGRY_SPOTTED", "ANGRY_STRIPED", "ANGRY_SNOWY");
    private static final Set<String> BABY_CAT_CONDITIONS = Set.of(
            "BABY_TABBY", "BABY_BLACK", "BABY_RED", "BABY_SIAMESE", "BABY_BRITISH_SHORTHAIR",
            "BABY_CALICO", "BABY_PERSIAN", "BABY_RAGDOLL", "BABY_WHITE", "BABY_JELLIE",
            "BABY_ALL_BLACK");

    @Test
    void bundledConfigurationContainsEveryBabyWolfAndCatVariant() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("mobs_head.yml")) {
            assertNotNull(stream, "Missing resource mobs_head.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));

            assertBabyHeads(config.getMapList("heads.WOLF"), BABY_WOLF_CONDITIONS);
            assertBabyHeads(config.getMapList("heads.WOLF"), ANGRY_WOLF_CONDITIONS);
            assertBabyHeads(config.getMapList("heads.CAT"), BABY_CAT_CONDITIONS);
        }
    }

    @Test
    void wolfConditionKeepsAngerForAdultsOnly() {
        assertEquals("PALE", MobHeadsRegistry.wolfCondition("PALE", true, false));
        assertEquals("ANGRY_PALE", MobHeadsRegistry.wolfCondition("PALE", true, true));
        assertEquals("BABY_PALE", MobHeadsRegistry.wolfCondition("PALE", false, false));
        assertEquals("BABY_PALE", MobHeadsRegistry.wolfCondition("PALE", false, true));
    }

    private static void assertBabyHeads(List<Map<?, ?>> heads, Set<String> expectedConditions) {
        Map<String, String> babyTextures = heads.stream()
                .filter(head -> expectedConditions.contains(head.get("condition")))
                .collect(Collectors.toMap(
                        head -> (String) head.get("condition"),
                        head -> (String) head.get("texture")));

        assertEquals(expectedConditions, babyTextures.keySet());
        babyTextures.forEach((condition, texture) -> {
            assertNotNull(texture, "Missing texture for " + condition);
            assertFalse(texture.isBlank(), "Blank texture for " + condition);
        });
    }
}
