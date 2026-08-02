package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.Test;

class MobHeadsCommandLookupTest {
    private static final String TEXTURE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQu"
                    + "bmV0L3RleHR1cmUvYWJjIn19fQ==";

    @Test
    void aSingleHeadCanBeSelectedWithoutVariant() {
        HeadConfig only = head(null);
        assertSame(only, MobHeadsRegistry.selectConfiguredHead(List.of(only), null));
    }

    @Test
    void multipleHeadsRequireAnExplicitVariant() {
        HeadConfig normal = head(null);
        HeadConfig baby = head("BABY");
        List<HeadConfig> heads = List.of(normal, baby);

        assertNull(MobHeadsRegistry.selectConfiguredHead(heads, null));
        assertSame(normal, MobHeadsRegistry.selectConfiguredHead(heads, "default"));
        assertSame(baby, MobHeadsRegistry.selectConfiguredHead(heads, "baby"));
        assertNull(MobHeadsRegistry.selectConfiguredHead(heads, "unknown"));
    }

    private static HeadConfig head(String condition) {
        return new HeadConfig(TEXTURE, "common", condition, "Test", null);
    }
}
