package net.laboulangerie.laboulangeriemmo.core.abilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AbilitiesDispatcherTest {
    @Test
    void computesRemainingCooldownInConfiguredUnit() {
        assertEquals(12, AbilitiesDispatcher.remainingCooldown(15, 3));
        assertEquals(1, AbilitiesDispatcher.remainingCooldown(15, 14));
        assertEquals(1, AbilitiesDispatcher.remainingCooldown(15, 15));
    }
}
