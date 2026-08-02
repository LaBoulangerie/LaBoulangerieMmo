package net.laboulangerie.laboulangeriemmo.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.sk89q.worldguard.protection.flags.StateFlag;

class WolrdGuardSupportTest {
    @Test
    void abilityUseFlagAllowsAbilitiesByDefault() {
        assertEquals(StateFlag.State.ALLOW, WolrdGuardSupport.USE_ABILITY_FLAG.getDefault());
    }

    @Test
    void allowsAbilitiesWhenFlagIsUnsetOrAllowed() {
        assertTrue(WolrdGuardSupport.isAbilityUseAllowed(null));
        assertTrue(WolrdGuardSupport.isAbilityUseAllowed(StateFlag.State.ALLOW));
    }

    @Test
    void blocksAbilitiesOnlyWhenFlagIsExplicitlyDenied() {
        assertFalse(WolrdGuardSupport.isAbilityUseAllowed(StateFlag.State.DENY));
    }
}
