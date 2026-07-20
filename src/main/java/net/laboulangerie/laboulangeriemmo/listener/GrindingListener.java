package net.laboulangerie.laboulangeriemmo.listener;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.TrialSpawnerSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.persistence.PersistentDataType;

import io.papermc.paper.event.entity.EntityFertilizeEggEvent;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.GrindingCategory;
import net.laboulangerie.laboulangeriemmo.core.XpMovementGuard;
import net.laboulangerie.laboulangeriemmo.utils.MythicMobsSupport;
import net.laboulangerie.laboulangeriemmo.utils.MythicMobContext;
import net.laboulangerie.laboulangeriemmo.utils.MythicMobPolicy;

public class GrindingListener implements Listener {
    private static final XpMovementGuard XP_MOVEMENT_GUARD = new XpMovementGuard();
    private static final byte NORMAL_TRIAL = 1;
    private static final byte OMINOUS_TRIAL = 2;

    private final NamespacedKey trialTypeKey;

    public GrindingListener() {
        trialTypeKey = new NamespacedKey(LaBoulangerieMmo.PLUGIN, "hunter_trial_type");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) != null) return;

        if (block.getState().getBlockData() instanceof Ageable) {
            Ageable ageable = ((Ageable) block.getState().getBlockData());
            if (ageable.getAge() != ageable.getMaximumAge() && !LaBoulangerieMmo.PLUGIN.getConfig()
                    .getStringList("ageable-ignored-blocks").contains(block.getType().toString()))
                return;
        }

        // Cave vines with berries check
        if (block.getType() == Material.CAVE_VINES_PLANT && !((CaveVinesPlant) block.getBlockData()).isBerries())
            return;

        giveReward(event.getPlayer(), GrindingCategory.BREAK, block.getType().toString(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityKill(EntityDeathEvent event) {
        if (event.isCancelled() || !(event.getEntity().getKiller() instanceof Player)) return;

        double trialMultiplier = getTrialMultiplier(event.getEntity());

        MythicMobContext mythicMob = null;
        if (LaBoulangerieMmo.MYTHICMOBS_SUPPORT) {
            try {
                mythicMob = MythicMobsSupport.inspect(event.getEntity()).orElse(null);
            } catch (RuntimeException | LinkageError e) {
                LaBoulangerieMmo.PLUGIN.getLogger().warning("MythicMobs reward failed: '" + e);
            }
        }

        if (mythicMob == null) {
            giveReward(event.getEntity().getKiller(), GrindingCategory.KILL, event.getEntity().getType().toString(),
                    event.getEntity().getEntitySpawnReason() == SpawnReason.SPAWNER, trialMultiplier);
        } else {
            giveMythicHunterReward(event.getEntity().getKiller(), mythicMob, trialMultiplier);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrialSpawnerSpawn(TrialSpawnerSpawnEvent event) {
        byte trialType = event.getTrialSpawner().isOminous() ? OMINOUS_TRIAL : NORMAL_TRIAL;
        event.getEntity().getPersistentDataContainer().set(trialTypeKey, PersistentDataType.BYTE, trialType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Material crafted = event.getRecipe().getResult().getType();

        giveReward(player, GrindingCategory.CRAFT, crafted.toString(), false);

        if (player.getStatistic(Statistic.CRAFT_ITEM, crafted) == 0) {
            giveReward(player, GrindingCategory.FIRST_CRAFT, crafted.toString(), false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreed(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player)) return;

        giveReward((Player) event.getBreeder(), GrindingCategory.BREED, event.getEntityType().toString(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEggFertilized(EntityFertilizeEggEvent event) {
        if (event.getBreeder() == null) return;

        giveReward(event.getBreeder(), GrindingCategory.BREED, event.getEntityType().toString(), false);
    }

    public static void giveReward(Player player, GrindingCategory category, String identifier, boolean isSpawnerMob) {
        giveReward(player, category, identifier, isSpawnerMob, 1.0);
    }

    static void giveReward(Player player, GrindingCategory category, String identifier, boolean isSpawnerMob,
            double rewardMultiplier) {
        if (player.getGameMode() == GameMode.CREATIVE) return;
        Set<String> keys =
                LaBoulangerieMmo.PLUGIN.getConfig().getConfigurationSection("talent-grinding").getKeys(false);

        for (String talentName : keys) {
            if (LaBoulangerieMmo.talentsRegistry.getTalent(talentName) == null) continue;

            FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();

            ConfigurationSection section = LaBoulangerieMmo.PLUGIN.getConfig()
                    .getConfigurationSection("talent-grinding." + talentName + "." + category.toString());

            if (section == null) continue;

            if (section.getKeys(false).contains(identifier)) {
                double xpAmount = section.getDouble(identifier);
                if (isSpawnerMob && category == GrindingCategory.KILL) {
                    if (config.contains("decrease-spawner-mobs-by." + identifier)) {
                        xpAmount -= config.getDouble("decrease-spawner-mobs-by." + identifier);
                    } else if (config.contains("decrease-spawner-mobs-by.*")) {
                        xpAmount -= config.getDouble("decrease-spawner-mobs-by.*");
                    }
                    xpAmount = xpAmount < 0 ? 0 : xpAmount;
                }
                if (category == GrindingCategory.KILL) xpAmount *= rewardMultiplier;
                if (xpAmount > 0 && XP_MOVEMENT_GUARD.canGainXp(player)) {
                    LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player).incrementXp(talentName, xpAmount);
                }
            }
        }
    }

    private static void giveMythicHunterReward(Player player, MythicMobContext context, double rewardMultiplier) {
        Double explicitAmount = MythicMobPolicy.hunterXp(context.internalName());
        if (explicitAmount != null) {
            giveTalentReward(player, "hunter", explicitAmount * rewardMultiplier);
            return;
        }

        if (context.disguiseProfile().isPresent()
                && giveConfiguredHunterKillReward(
                        player, context.disguiseProfile().get().mobType(), rewardMultiplier)) return;
        giveConfiguredHunterKillReward(player, context.baseProfile().mobType(), rewardMultiplier);
    }

    private static boolean giveConfiguredHunterKillReward(Player player, String identifier, double rewardMultiplier) {
        ConfigurationSection section = LaBoulangerieMmo.PLUGIN.getConfig()
                .getConfigurationSection("talent-grinding.hunter." + GrindingCategory.KILL);
        if (section == null || !section.contains(identifier)) return false;
        giveTalentReward(player, "hunter", section.getDouble(identifier) * rewardMultiplier);
        return true;
    }

    private double getTrialMultiplier(org.bukkit.entity.Entity entity) {
        Byte trialType = entity.getPersistentDataContainer().get(trialTypeKey, PersistentDataType.BYTE);
        if (trialType == null) return 1.0;
        return configuredTrialMultiplier(LaBoulangerieMmo.PLUGIN.getConfig(), trialType == OMINOUS_TRIAL);
    }

    static double configuredTrialMultiplier(ConfigurationSection config, boolean ominous) {
        String key = "talent-grinding.hunter.trial-multipliers." + (ominous ? "ominous" : "normal");
        return config.getDouble(key, ominous ? 2.0 : 1.5);
    }

    private static void giveTalentReward(Player player, String talentName, double amount) {
        if (player.getGameMode() == GameMode.CREATIVE || amount <= 0 || !Double.isFinite(amount)) return;
        if (LaBoulangerieMmo.talentsRegistry.getTalent(talentName) == null) return;
        if (XP_MOVEMENT_GUARD.canGainXp(player)) {
            LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player).incrementXp(talentName, amount);
        }
    }
}
