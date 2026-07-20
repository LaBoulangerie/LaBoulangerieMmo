package net.laboulangerie.laboulangeriemmo.core.rareloot;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.laboulangerie.laboulangeriemmo.api.rareloot.RareLootAction;

public record RareLootRegistry(
        Map<String, RareLootItemDefinition> items,
        Map<String, ChanceTable> chanceProfiles,
        Map<RareLootAction, List<RareLootRule>> rules) {

    public static RareLootRegistry empty() {
        return new RareLootRegistry(Map.of(), Map.of(), new EnumMap<>(RareLootAction.class));
    }

    public List<RareLootRule> rulesFor(RareLootAction action) {
        return rules.getOrDefault(action, List.of());
    }

    public int ruleCount() {
        return rules.values().stream().mapToInt(List::size).sum();
    }
}
