package net.laboulangerie.laboulangeriemmo.core.protection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import fr.minelet.api.protection.IProtectionService;
import fr.minelet.api.protection.BlockAction;
import fr.minelet.api.protection.ProtectionRequest;
import fr.minelet.api.protection.ProtectionResult;

class MineLetTalentProtectionTest {
    @Test
    void delegatesBreakPlaceAndIgniteChecksToMineLet() {
        JavaPlugin plugin = mock(JavaPlugin.class);
        IProtectionService service = mock(IProtectionService.class);
        ProtectionResult result = new ProtectionResult(
                true, true, BlockAction.BREAK, null, "destroy", null);
        Player player = mock(Player.class);
        Block block = mock(Block.class);
        when(service.check(any(ProtectionRequest.class))).thenReturn(result);

        MineLetTalentProtection protection = new MineLetTalentProtection(plugin, service);

        assertTrue(protection.canBreak(player, block));
        assertTrue(protection.canPlace(player, block, Material.WHEAT));
        assertTrue(protection.canIgnite(block));
        verify(service, times(3)).check(any(ProtectionRequest.class));
    }

    @Test
    void propagatesDeniedResult() {
        JavaPlugin plugin = mock(JavaPlugin.class);
        IProtectionService service = mock(IProtectionService.class);
        ProtectionResult result = new ProtectionResult(
                false, true, BlockAction.BREAK, null, "destroy", "protection.denied");
        when(service.check(any(ProtectionRequest.class))).thenReturn(result);

        MineLetTalentProtection protection = new MineLetTalentProtection(plugin, service);

        assertFalse(protection.canBreak(mock(Player.class), mock(Block.class)));
    }

    @Test
    void deniesModificationWhenMineLetThrows() {
        JavaPlugin plugin = mock(JavaPlugin.class);
        when(plugin.getLogger()).thenReturn(Logger.getAnonymousLogger());
        IProtectionService service = mock(IProtectionService.class);
        when(service.check(any(ProtectionRequest.class)))
                .thenThrow(new IllegalStateException("MineLet unavailable"));

        MineLetTalentProtection protection = new MineLetTalentProtection(plugin, service);

        assertFalse(protection.canIgnite(mock(Block.class)));
    }

    @Test
    void allowAllFallbackKeepsTalentsWorkingWithoutMineLet() {
        AllowAllTalentProtection protection = new AllowAllTalentProtection();

        assertTrue(protection.canBreak(null, null));
        assertTrue(protection.canPlace(null, null, Material.WHEAT));
        assertTrue(protection.canIgnite(null));
    }
}
