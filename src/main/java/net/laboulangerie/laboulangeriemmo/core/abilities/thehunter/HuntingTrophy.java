package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter;

import java.util.Set;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.utils.MythicMobContext;
import net.laboulangerie.laboulangeriemmo.utils.MythicMobPolicy;
import net.laboulangerie.laboulangeriemmo.utils.MythicMobsSupport;

public class HuntingTrophy extends AbilityExecutor {

    private static final Set<EntityType> VANILLA_HEAD_MOBS = Set.of(
            EntityType.SKELETON,
            EntityType.WITHER_SKELETON,
            EntityType.ZOMBIE,
            EntityType.CREEPER,
            EntityType.PIGLIN,
            EntityType.ENDER_DRAGON);

    private static final float TIER_3_CHANCE = 0.05f;
    private static final float TIER_2_CHANCE = 0.03f;
    private static final float TIER_1_CHANCE = 0.01f;

    public HuntingTrophy(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        EntityDeathEvent event = (EntityDeathEvent) baseEvent;
        Player killer = event.getEntity().getKiller();
        if (killer == null) return false;
        MobVisualProfile profile = resolveProfile(event.getEntity());
        if (profile == null || isVanillaHeadMob(profile.mobType())) return false;
        return MobHeadsRegistry.hasHead(profile.mobType());
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        EntityDeathEvent event = (EntityDeathEvent) baseEvent;
        LivingEntity entity = event.getEntity();

        float baseChance;
        if (level >= getTier(2)) {
            baseChance = TIER_3_CHANCE;
        } else if (level >= getTier(1)) {
            baseChance = TIER_2_CHANCE;
        } else {
            baseChance = TIER_1_CHANCE;
        }

        MobVisualProfile profile = resolveProfile(entity);
        if (profile == null) return;
        HeadConfig headConfig = MobHeadsRegistry.getHead(profile);

        if (headConfig == null) return;

        // Apply rarity multiplier
        float finalChance = baseChance * headConfig.getRarityMultiplier();
        double roll = Math.random();

        if (roll <= finalChance) {
            ItemStack head = headConfig.createHead();
            entity.getWorld().dropItemNaturally(entity.getLocation(), head);
        }
    }

    private static MobVisualProfile resolveProfile(LivingEntity entity) {
        if (!LaBoulangerieMmo.MYTHICMOBS_SUPPORT) return MobHeadsRegistry.getVisualProfile(entity);
        try {
            var context = MythicMobsSupport.inspect(entity);
            if (context.isEmpty()) return MobHeadsRegistry.getVisualProfile(entity);
            MythicMobContext mythic = context.get();
            return switch (MythicMobPolicy.headMode(mythic.internalName())) {
                case NONE -> null;
                case BASE -> mythic.baseProfile();
                case DISGUISE -> mythic.disguiseProfile().orElse(null);
            };
        } catch (RuntimeException | LinkageError exception) {
            LaBoulangerieMmo.PLUGIN.getLogger().warning(
                    "MythicMobs head lookup failed for " + entity.getUniqueId() + ": " + exception.getMessage());
            return null;
        }
    }

    private static boolean isVanillaHeadMob(String type) {
        return VANILLA_HEAD_MOBS.stream().anyMatch(entityType -> entityType.name().equals(type));
    }
}
