package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;

class TimberTest {
    @Test
    void usesRangeForCurrentTier() {
        AbilityArchetype archetype = new AbilityArchetype();
        archetype.requiredLevel = 35;
        archetype.tiers = List.of(35, 65, 95);
        Timber timber = new Timber(archetype);

        assertEquals(3, timber.rangeForLevel(35));
        assertEquals(3, timber.rangeForLevel(64));
        assertEquals(5, timber.rangeForLevel(65));
        assertEquals(5, timber.rangeForLevel(94));
        assertEquals(7, timber.rangeForLevel(95));
        assertEquals(7, timber.rangeForLevel(100));
    }
}
