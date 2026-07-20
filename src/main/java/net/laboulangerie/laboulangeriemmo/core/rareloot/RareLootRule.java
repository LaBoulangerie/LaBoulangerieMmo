package net.laboulangerie.laboulangeriemmo.core.rareloot;

import net.laboulangerie.laboulangeriemmo.api.rareloot.RareLootAction;

public record RareLootRule(
        String id,
        String job,
        RareLootAction action,
        RareLootConditions conditions,
        RareLootItemDefinition item,
        int minimumAmount,
        int maximumAmount,
        ChanceTable chance,
        boolean allowCreative,
        boolean allowPlacedBlocks,
        boolean allowSpawnerMobs) {}
