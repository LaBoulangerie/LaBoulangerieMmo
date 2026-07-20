package net.laboulangerie.laboulangeriemmo.core.rareloot;

import net.laboulangerie.laboulangeriemmo.api.rareloot.RareLootAction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

public record RareLootContext(
        RareLootAction action,
        Player player,
        Location location,
        Material blockType,
        EntityType entityType,
        String mythicMob,
        ItemStack tool,
        Biome biome,
        SpawnReason spawnReason,
        Ageable ageable,
        boolean placedBlock) {}
