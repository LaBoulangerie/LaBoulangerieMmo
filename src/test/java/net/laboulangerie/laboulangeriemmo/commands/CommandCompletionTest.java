package net.laboulangerie.laboulangeriemmo.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class CommandCompletionTest {
    @Test
    void completionIsCaseInsensitiveAndPrefixFiltered() {
        assertEquals(List.of("PIG", "PIGLIN"),
                MasteryItemCommand.matching(List.of("PIG", "PIGLIN", "WOLF"), "pi"));
        assertEquals(List.of("DEFAULT"),
                MasteryItemCommand.matching(List.of("BABY", "DEFAULT"), "def"));
    }
}
