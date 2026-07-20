package net.laboulangerie.laboulangeriemmo.core.rareloot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.TreeMap;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

class ChanceTableTest {
    @Test
    void selectsGreatestReachedThreshold() {
        TreeMap<Integer, Double> thresholds = new TreeMap<>();
        thresholds.put(10, 1.0);
        thresholds.put(11, 3.0);
        thresholds.put(75, 5.0);
        ChanceTable table = new ChanceTable(0, thresholds);

        assertEquals(0, table.chanceAt(9));
        assertEquals(1, table.chanceAt(10));
        assertEquals(3, table.chanceAt(11));
        assertEquals(3, table.chanceAt(74));
        assertEquals(5, table.chanceAt(75));
        assertEquals(5, table.chanceAt(100));
    }

    @Test
    void fixedChanceAppliesAtEveryLevel() {
        ChanceTable table = ChanceTable.fixed(2.5);
        assertEquals(2.5, table.chanceAt(0));
        assertEquals(2.5, table.chanceAt(100));
    }

    @Test
    void rejectsInvalidChancesAndLevels() {
        assertThrows(IllegalArgumentException.class, () -> ChanceTable.fixed(-0.01));
        assertThrows(IllegalArgumentException.class, () -> ChanceTable.fixed(100.01));
        TreeMap<Integer, Double> invalidLevel = new TreeMap<>();
        invalidLevel.put(-1, 1.0);
        assertThrows(IllegalArgumentException.class, () -> new ChanceTable(0, invalidLevel));
    }

    @Test
    void itemTextIsNotItalicByDefaultButAllowsExplicitItalic() {
        var normal = RareLootConfigLoader.deserializeItemText("<aqua>Os renforcé");
        assertEquals(TextDecoration.State.FALSE, normal.decoration(TextDecoration.ITALIC));

        var explicit = RareLootConfigLoader.deserializeItemText("<italic>Fragment ancien</italic>");
        assertEquals(TextDecoration.State.FALSE, explicit.decoration(TextDecoration.ITALIC));
        assertEquals(TextDecoration.State.TRUE, explicit.children().getFirst().decoration(TextDecoration.ITALIC));
    }
}
