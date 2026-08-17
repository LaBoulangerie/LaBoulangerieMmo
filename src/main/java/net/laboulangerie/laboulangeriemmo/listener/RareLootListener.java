package net.laboulangerie.laboulangeriemmo.listener;

import java.util.EnumMap;
import java.util.Map;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.rareloot.RareLootAction;
import net.laboulangerie.laboulangeriemmo.core.rareloot.RareLootContext;
import net.laboulangerie.laboulangeriemmo.core.rareloot.RareLootEngine;
import net.laboulangerie.laboulangeriemmo.core.rareloot.RareLootManager;
import net.laboulangerie.laboulangeriemmo.utils.MythicMobsSupport;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class RareLootListener implements Listener {
    private static final Map<Material, Material> STRIPPED = strippedMaterials();
    private final RareLootManager manager;
    private final RareLootEngine engine;

    public RareLootListener(RareLootManager manager) {
        this.manager = manager;
        this.engine = manager.engine();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        boolean placed = LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) != null;
        manager.processBlockBreak(event.getPlayer(), block, placed);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStrip(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND
                || event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();
        Material expected = STRIPPED.get(block.getType());
        if (expected == null || !isAxe(event.getPlayer().getInventory().getItemInMainHand().getType())) return;
        Material original = block.getType();
        boolean placed = LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) != null;
        var location = block.getLocation();
        BukkitScheduler.runNext(() -> {
            Block current = location.getBlock();
            if (current.getType() == expected) {
                process(RareLootAction.LOG_STRIP, event.getPlayer(), current, original, null, null, null, placed);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;
        String mythic = null;
        if (LaBoulangerieMmo.MYTHICMOBS_SUPPORT) {
            try {
                mythic = MythicMobsSupport.inspect(event.getEntity()).map(context -> context.internalName()).orElse(null);
            } catch (RuntimeException | LinkageError exception) {
                LaBoulangerieMmo.PLUGIN.getLogger().warning("Rare-loot MythicMobs inspection failed: " + exception.getMessage());
            }
        }
        var context = new RareLootContext(RareLootAction.KILL, player, event.getEntity().getLocation(), null,
                event.getEntityType(), mythic, player.getInventory().getItemInMainHand(),
                event.getEntity().getLocation().getBlock().getBiome(), event.getEntity().getEntitySpawnReason(),
                event.getEntity() instanceof org.bukkit.entity.Ageable ageable ? ageable : null, false);
        engine.process(context);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreed(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player player)) return;
        var context = new RareLootContext(RareLootAction.BREED, player, event.getEntity().getLocation(), null,
                event.getEntityType(), null, player.getInventory().getItemInMainHand(),
                event.getEntity().getLocation().getBlock().getBiome(), event.getEntity().getEntitySpawnReason(),
                event.getEntity() instanceof org.bukkit.entity.Ageable ageable ? ageable : null, false);
        engine.process(context);
    }

    private void process(RareLootAction action, Player player, Block block, Material source, String mythic,
            org.bukkit.entity.EntityType entityType,
            org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason spawnReason, boolean placed) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        engine.process(new RareLootContext(action, player, block.getLocation().add(0.5, 0.5, 0.5), source,
                entityType, mythic, tool, block.getBiome(), spawnReason, null, placed));
    }

    private boolean isAxe(Material material) {
        return material.name().endsWith("_AXE");
    }

    private static Map<Material, Material> strippedMaterials() {
        Map<Material, Material> values = new EnumMap<>(Material.class);
        for (Material material : Material.values()) {
            if (material.name().startsWith("STRIPPED_")) continue;
            Material stripped = Material.matchMaterial("STRIPPED_" + material.name());
            if (stripped != null) values.put(material, stripped);
        }
        return Map.copyOf(values);
    }

    private static final class BukkitScheduler {
        private static void runNext(Runnable runnable) {
            LaBoulangerieMmo.PLUGIN.getServer().getScheduler().runTask(LaBoulangerieMmo.PLUGIN, runnable);
        }
    }
}
