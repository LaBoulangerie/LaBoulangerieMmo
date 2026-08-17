package net.laboulangerie.laboulangeriemmo.core.rareloot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.laboulangerie.laboulangeriemmo.api.rareloot.RareLootAction;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.junit.jupiter.api.Test;

class RareLootManagerTest {
    @Test
    void onlyMatureCropsAreClassifiedAsHarvests() {
        Ageable growingCrop = mock(Ageable.class);
        when(growingCrop.getAge()).thenReturn(6);
        when(growingCrop.getMaximumAge()).thenReturn(7);
        assertEquals(RareLootAction.BLOCK_BREAK, RareLootManager.actionFor(growingCrop));

        Ageable matureCrop = mock(Ageable.class);
        when(matureCrop.getAge()).thenReturn(7);
        when(matureCrop.getMaximumAge()).thenReturn(7);
        assertEquals(RareLootAction.HARVEST, RareLootManager.actionFor(matureCrop));
    }

    @Test
    void caveVinesRequireBerriesToBeHarvests() {
        CaveVinesPlant emptyVines = mock(CaveVinesPlant.class);
        when(emptyVines.isBerries()).thenReturn(false);
        assertEquals(RareLootAction.BLOCK_BREAK, RareLootManager.actionFor(emptyVines));

        CaveVinesPlant berryVines = mock(CaveVinesPlant.class);
        when(berryVines.isBerries()).thenReturn(true);
        assertEquals(RareLootAction.HARVEST, RareLootManager.actionFor(berryVines));

        assertEquals(RareLootAction.BLOCK_BREAK, RareLootManager.actionFor(mock(BlockData.class)));
    }
}
