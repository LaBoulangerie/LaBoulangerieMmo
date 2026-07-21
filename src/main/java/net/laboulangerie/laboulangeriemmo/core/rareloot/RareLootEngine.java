package net.laboulangerie.laboulangeriemmo.core.rareloot;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.rareloot.RareLootItemProviderRegistry;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class RareLootEngine {
    private final RareLootItemProviderRegistry providers;
    private final RandomGenerator random;
    private final Set<String> unavailableItemsLogged = new HashSet<>();
    private volatile RareLootRegistry registry = RareLootRegistry.empty();

    public RareLootEngine(RareLootItemProviderRegistry providers) {
        this(providers, new SplittableRandom());
    }

    RareLootEngine(RareLootItemProviderRegistry providers, RandomGenerator random) {
        this.providers = providers;
        this.random = random;
    }

    public void replaceRegistry(RareLootRegistry registry) {
        this.registry = registry;
        unavailableItemsLogged.clear();
    }

    public RareLootRegistry registry() {
        return registry;
    }

    public void process(RareLootContext context) {
        for (RareLootRule rule : registry.rulesFor(context.action())) {
            if (!eligible(rule, context)) continue;
            MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(context.player());
            var talent = mmoPlayer.getTalent(rule.job());
            if (talent == null) continue;
            double chance = rule.chance().chanceAt(talent.getLevel());
            if (chance <= 0 || random.nextDouble(100.0) >= chance) continue;
            ItemStack item = providers.create(rule.item().provider(), rule.item().providerItemId()).orElse(null);
            if (item == null) {
                String key = rule.item().provider() + ':' + rule.item().providerItemId();
                if (unavailableItemsLogged.add(key)) {
                    LaBoulangerieMmo.PLUGIN.getLogger().warning("Rare-loot item could not be resolved: " + key);
                }
                continue;
            }
            int amount = rule.minimumAmount() == rule.maximumAmount() ? rule.minimumAmount()
                    : random.nextInt(rule.minimumAmount(), rule.maximumAmount() + 1);
            drop(context, item, amount);
            String message = LaBoulangerieMmo.PLUGIN.getConfig().getString(
                    "lang.messages.mastery-loot",
                    "<yellow>Vos yeux aguerris vous ont permis de trouver quelque chose !");
            context.player().sendMessage(MiniMessage.miniMessage().deserialize(message));
        }
    }

    private boolean eligible(RareLootRule rule, RareLootContext context) {
        if (context.player().getGameMode() == GameMode.CREATIVE && !rule.allowCreative()) return false;
        if (context.placedBlock() && !rule.allowPlacedBlocks()) return false;
        if (context.spawnReason() == org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER
                && !rule.allowSpawnerMobs()) return false;
        RareLootConditions conditions = rule.conditions();
        if (!matchesMaterial(context.blockType(), conditions.blockTypes(), conditions.blockTags(), Tag.REGISTRY_BLOCKS))
            return false;
        if (!conditions.entityTypes().isEmpty()
                && (context.entityType() == null || !conditions.entityTypes().contains(context.entityType()))) return false;
        if (!conditions.mythicMobs().isEmpty()
                && (context.mythicMob() == null
                        || !conditions.mythicMobs().contains(context.mythicMob().toUpperCase(Locale.ROOT)))) return false;
        Material tool = context.tool() == null ? null : context.tool().getType();
        if (!matchesMaterial(tool, conditions.toolTypes(), conditions.toolTags(), Tag.REGISTRY_ITEMS)) return false;
        if (!conditions.enchantments().isEmpty()) {
            if (context.tool() == null) return false;
            for (var entry : conditions.enchantments().entrySet()) {
                Enchantment enchantment = org.bukkit.Registry.ENCHANTMENT.get(entry.getKey());
                if (enchantment == null || context.tool().getEnchantmentLevel(enchantment) < entry.getValue()) return false;
            }
        }
        String world = context.location().getWorld().getName().toLowerCase(Locale.ROOT);
        if (!conditions.worlds().isEmpty() && !conditions.worlds().contains(world)) return false;
        if (conditions.excludedWorlds().contains(world)) return false;
        String biome = context.biome() == null ? "" : context.biome().toString().toUpperCase(Locale.ROOT);
        if (!conditions.biomes().isEmpty() && !conditions.biomes().contains(biome)) return false;
        if (conditions.excludedBiomes().contains(biome)) return false;
        for (String permission : conditions.permissions()) if (!context.player().hasPermission(permission)) return false;
        if (!conditions.spawnReasons().isEmpty()
                && (context.spawnReason() == null || !conditions.spawnReasons().contains(context.spawnReason()))) return false;
        if (conditions.adult() != null
                && (context.ageable() == null || context.ageable().isAdult() != conditions.adult())) return false;
        return true;
    }

    private boolean matchesMaterial(Material material, Set<Material> values, Set<NamespacedKey> tags, String registry) {
        if (values.isEmpty() && tags.isEmpty()) return true;
        if (material == null) return false;
        if (values.contains(material)) return true;
        for (NamespacedKey key : tags) {
            Tag<Material> tag = Bukkit.getTag(registry, key, Material.class);
            if (tag != null && tag.isTagged(material)) return true;
        }
        return false;
    }

    private void drop(RareLootContext context, ItemStack template, int amount) {
        int remaining = amount;
        while (remaining > 0) {
            ItemStack stack = template.clone();
            int size = Math.min(remaining, stack.getMaxStackSize());
            stack.setAmount(size);
            context.location().getWorld().dropItemNaturally(context.location(), stack);
            remaining -= size;
        }
    }
}
