package net.laboulangerie.laboulangeriemmo.core.rareloot;

import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public record RareLootConditions(
        Set<Material> blockTypes,
        Set<NamespacedKey> blockTags,
        Set<EntityType> entityTypes,
        Set<String> mythicMobs,
        Set<Material> toolTypes,
        Set<NamespacedKey> toolTags,
        Map<NamespacedKey, Integer> enchantments,
        Set<String> worlds,
        Set<String> excludedWorlds,
        Set<String> biomes,
        Set<String> excludedBiomes,
        Set<String> permissions,
        Set<SpawnReason> spawnReasons,
        Boolean adult) {

    public static RareLootConditions empty() {
        return new RareLootConditions(Set.of(), Set.of(), Set.of(), Set.of(), Set.of(), Set.of(), Map.of(), Set.of(), Set.of(),
                Set.of(), Set.of(), Set.of(), Set.of(), null);
    }
}
