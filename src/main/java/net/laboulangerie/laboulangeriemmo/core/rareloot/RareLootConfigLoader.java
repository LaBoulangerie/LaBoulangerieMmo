package net.laboulangerie.laboulangeriemmo.core.rareloot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.rareloot.RareLootAction;
import net.laboulangerie.laboulangeriemmo.api.rareloot.RareLootItemProviderRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class RareLootConfigLoader {
    public record Result(RareLootRegistry registry, Map<String, ItemStack> nativeItems, List<String> warnings) {}

    private final LaBoulangerieMmo plugin;
    private final RareLootItemProviderRegistry providers;
    private final File directory;

    public RareLootConfigLoader(LaBoulangerieMmo plugin, RareLootItemProviderRegistry providers) {
        this.plugin = plugin;
        this.providers = providers;
        this.directory = new File(plugin.getDataFolder(), "rare-loots");
    }

    public Result load() throws IOException, InvalidConfigurationException {
        YamlConfiguration settings = loadYaml(new File(directory, "settings.yml"));
        YamlConfiguration itemConfig = loadYaml(new File(directory, "items.yml"));
        YamlConfiguration chanceConfig = loadYaml(new File(directory, "chances.yml"));

        Map<String, ItemStack> nativeItems = new LinkedHashMap<>();
        Map<String, RareLootItemDefinition> items = parseItems(itemConfig, nativeItems);
        Map<String, ChanceTable> profiles = parseProfiles(chanceConfig);
        Map<RareLootAction, List<RareLootRule>> rules = new EnumMap<>(RareLootAction.class);
        List<String> warnings = new ArrayList<>();

        File jobsDirectory = new File(directory, "jobs");
        File[] files = jobsDirectory.listFiles(file -> file.isFile()
                && (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")));
        if (files != null) {
            java.util.Arrays.sort(files, java.util.Comparator.comparing(File::getName));
            for (File file : files) parseJob(file, settings, items, profiles, rules, warnings);
        }
        Map<RareLootAction, List<RareLootRule>> immutableRules = new EnumMap<>(RareLootAction.class);
        rules.forEach((action, value) -> immutableRules.put(action, List.copyOf(value)));
        return new Result(new RareLootRegistry(Map.copyOf(items), Map.copyOf(profiles), immutableRules),
                Map.copyOf(nativeItems), List.copyOf(warnings));
    }

    private Map<String, RareLootItemDefinition> parseItems(YamlConfiguration config) {
        ConfigurationSection root = requiredSection(config, "items", "items.yml");
        Map<String, RareLootItemDefinition> definitions = new LinkedHashMap<>();
        Map<String, ItemStack> nativeItems = currentNativeItems;
        for (String id : root.getKeys(false)) {
            ConfigurationSection section = requiredSection(root, id, "items.yml");
            String provider = section.getString("provider", "native").toLowerCase(Locale.ROOT);
            if (provider.equals("native")) {
                nativeItems.put(id, parseNativeItem(id, section));
                definitions.put(id, new RareLootItemDefinition(provider, id));
            } else {
                String providerId = requiredString(section, "id", "items." + id);
                definitions.put(id, new RareLootItemDefinition(provider, providerId));
            }
        }
        return definitions;
    }

    private Map<String, ItemStack> currentNativeItems;

    private Map<String, RareLootItemDefinition> parseItems(YamlConfiguration config, Map<String, ItemStack> target) {
        currentNativeItems = target;
        try {
            return parseItems(config);
        } finally {
            currentNativeItems = null;
        }
    }

    private ItemStack parseNativeItem(String id, ConfigurationSection section) {
        Material material = material(requiredString(section, "material", "items." + id));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String name = section.getString("name");
        if (name != null) meta.displayName(deserializeItemText(name));
        if (section.isList("lore")) {
            meta.lore(section.getStringList("lore").stream().map(RareLootConfigLoader::deserializeItemText).toList());
        }
        if (section.contains("custom-model-data")) meta.setCustomModelData(section.getInt("custom-model-data"));
        if (section.contains("glint")) meta.setEnchantmentGlintOverride(section.getBoolean("glint"));
        meta.setUnbreakable(section.getBoolean("unbreakable", false));
        for (String raw : section.getStringList("item-flags")) {
            try {
                meta.addItemFlags(ItemFlag.valueOf(raw.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException exception) {
                throw invalid("items." + id + ".item-flags", "unknown item flag " + raw);
            }
        }
        ConfigurationSection enchantments = section.getConfigurationSection("enchantments");
        if (enchantments != null) {
            for (String raw : enchantments.getKeys(false)) {
                NamespacedKey key = registryKey(raw, "items." + id + ".enchantments");
                Enchantment enchantment = Registry.ENCHANTMENT.get(key);
                if (enchantment == null) throw invalid("items." + id + ".enchantments", "unknown enchantment " + raw);
                meta.addEnchant(enchantment, enchantments.getInt(raw), true);
            }
        }
        ConfigurationSection data = section.getConfigurationSection("persistent-data");
        if (data != null) {
            for (String raw : data.getKeys(false)) {
                meta.getPersistentDataContainer().set(key(raw, "items." + id + ".persistent-data"),
                        PersistentDataType.STRING, String.valueOf(data.get(raw)));
            }
        }
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "rare_loot_id"),
                PersistentDataType.STRING, id);
        item.setItemMeta(meta);
        return item;
    }

    static Component deserializeItemText(String input) {
        return Component.empty().decoration(TextDecoration.ITALIC, false)
                .append(MiniMessage.miniMessage().deserialize(input));
    }

    private Map<String, ChanceTable> parseProfiles(YamlConfiguration config) {
        ConfigurationSection root = requiredSection(config, "profiles", "chances.yml");
        Map<String, ChanceTable> profiles = new LinkedHashMap<>();
        for (String id : root.getKeys(false)) profiles.put(id, parseChance(root.get(id), "profiles." + id));
        return profiles;
    }

    private void parseJob(File file, YamlConfiguration settings, Map<String, RareLootItemDefinition> items,
            Map<String, ChanceTable> profiles, Map<RareLootAction, List<RareLootRule>> rules, List<String> warnings)
            throws IOException, InvalidConfigurationException {
        YamlConfiguration config = loadYaml(file);
        String job = requiredString(config, "job", file.getName());
        if (LaBoulangerieMmo.talentsRegistry.getTalent(job) == null) {
            throw invalid(file.getName() + ".job", "unknown job " + job);
        }
        ConfigurationSection root = requiredSection(config, "rules", file.getName());
        boolean defaultCreative = settings.getBoolean("anti-farm.allow-creative", false);
        boolean defaultPlaced = settings.getBoolean("anti-farm.allow-placed-blocks", false);
        boolean defaultSpawner = settings.getBoolean("anti-farm.allow-spawner-mobs", false);
        for (String id : root.getKeys(false)) {
            ConfigurationSection section = requiredSection(root, id, file.getName());
            if (!section.getBoolean("enabled", true)) continue;
            String path = file.getName() + ".rules." + id;
            RareLootAction action;
            try {
                action = RareLootAction.valueOf(requiredString(section, "action", path).toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw invalid(path + ".action", "unknown action");
            }
            ConfigurationSection loot = requiredSection(section, "loot", path);
            String itemId = requiredString(loot, "item", path + ".loot");
            RareLootItemDefinition item = items.get(itemId);
            if (item == null) throw invalid(path + ".loot.item", "unknown item " + itemId);
            if (!providers.contains(item.provider()) && !item.provider().equals("native")) {
                warnings.add(path + " disabled: provider '" + item.provider() + "' is unavailable");
                continue;
            }
            int[] amount = parseAmount(loot, path);
            ChanceTable chance;
            if (loot.contains("chance-profile")) {
                String profile = loot.getString("chance-profile");
                chance = profiles.get(profile);
                if (chance == null) throw invalid(path + ".loot.chance-profile", "unknown profile " + profile);
            } else if (loot.contains("chance")) {
                chance = parseChance(loot.get("chance"), path + ".loot.chance");
            } else {
                throw invalid(path + ".loot", "chance or chance-profile is required");
            }
            ConfigurationSection anti = section.getConfigurationSection("anti-farm");
            boolean creative = anti == null ? defaultCreative : anti.getBoolean("allow-creative", defaultCreative);
            boolean placed = anti == null ? defaultPlaced : anti.getBoolean("allow-placed-blocks", defaultPlaced);
            boolean spawner = anti == null ? defaultSpawner : anti.getBoolean("allow-spawner-mobs", defaultSpawner);
            RareLootRule rule = new RareLootRule(id, job, action,
                    parseConditions(section.getConfigurationSection("conditions"), path), item, amount[0], amount[1],
                    chance, creative, placed, spawner);
            rules.computeIfAbsent(action, ignored -> new ArrayList<>()).add(rule);
        }
    }

    private RareLootConditions parseConditions(ConfigurationSection section, String path) {
        if (section == null) return RareLootConditions.empty();
        return new RareLootConditions(
                materials(section.getStringList("block-types"), path + ".conditions.block-types"),
                keys(section.getStringList("block-tags"), path + ".conditions.block-tags"),
                enumValues(section.getStringList("entity-types"), EntityType.class, path + ".conditions.entity-types"),
                upperStrings(section.getStringList("mythic-mobs")),
                materials(section.getStringList("tool-types"), path + ".conditions.tool-types"),
                keys(section.getStringList("tool-tags"), path + ".conditions.tool-tags"),
                enchantments(section.getConfigurationSection("enchantments"), path),
                lowerStrings(section.getStringList("worlds")), lowerStrings(section.getStringList("excluded-worlds")),
                upperStrings(section.getStringList("biomes")), upperStrings(section.getStringList("excluded-biomes")),
                Set.copyOf(section.getStringList("permissions")),
                enumValues(section.getStringList("spawn-reasons"), SpawnReason.class, path + ".conditions.spawn-reasons"),
                section.contains("adult") ? section.getBoolean("adult") : null);
    }

    private Map<NamespacedKey, Integer> enchantments(ConfigurationSection section, String path) {
        if (section == null) return Map.of();
        Map<NamespacedKey, Integer> values = new LinkedHashMap<>();
        for (String raw : section.getKeys(false)) values.put(registryKey(raw, path + ".conditions.enchantments"), section.getInt(raw));
        return Map.copyOf(values);
    }

    private ChanceTable parseChance(Object value, String path) {
        if (value instanceof Number number) return ChanceTable.fixed(number.doubleValue());
        if (!(value instanceof ConfigurationSection section)) throw invalid(path, "expected number or section");
        double defaultChance = section.getDouble("default", 0);
        NavigableMap<Integer, Double> levels = new TreeMap<>();
        ConfigurationSection thresholds = section.getConfigurationSection("levels");
        if (thresholds != null) {
            for (String raw : thresholds.getKeys(false)) {
                try {
                    levels.put(Integer.parseInt(raw), thresholds.getDouble(raw));
                } catch (NumberFormatException exception) {
                    throw invalid(path + ".levels." + raw, "level must be an integer");
                }
            }
        }
        try {
            return new ChanceTable(defaultChance, levels);
        } catch (IllegalArgumentException exception) {
            throw invalid(path, exception.getMessage());
        }
    }

    private int[] parseAmount(ConfigurationSection loot, String path) {
        if (loot.isConfigurationSection("amount")) {
            ConfigurationSection amount = loot.getConfigurationSection("amount");
            int min = amount.getInt("min", 1);
            int max = amount.getInt("max", min);
            if (min < 1 || max < min) throw invalid(path + ".loot.amount", "expected 1 <= min <= max");
            return new int[] {min, max};
        }
        int amount = loot.getInt("amount", 1);
        if (amount < 1) throw invalid(path + ".loot.amount", "amount must be positive");
        return new int[] {amount, amount};
    }

    private YamlConfiguration loadYaml(File file) throws IOException, InvalidConfigurationException {
        if (!file.isFile()) throw new IOException("Missing rare-loot configuration: " + file.getPath());
        YamlConfiguration config = new YamlConfiguration();
        config.load(file);
        return config;
    }

    private Material material(String raw) {
        Material value = Material.matchMaterial(raw);
        if (value == null) throw invalid(raw, "unknown material");
        return value;
    }

    private Set<Material> materials(List<String> values, String path) {
        Set<Material> result = new LinkedHashSet<>();
        for (String value : values) {
            Material material = Material.matchMaterial(value);
            if (material == null) throw invalid(path, "unknown material " + value);
            result.add(material);
        }
        return Set.copyOf(result);
    }

    private Set<NamespacedKey> keys(List<String> values, String path) {
        Set<NamespacedKey> result = new LinkedHashSet<>();
        for (String value : values) result.add(registryKey(value.startsWith("#") ? value.substring(1) : value, path));
        return Set.copyOf(result);
    }

    private NamespacedKey registryKey(String raw, String path) {
        String qualified = raw.contains(":") ? raw : "minecraft:" + raw;
        NamespacedKey value = NamespacedKey.fromString(qualified);
        if (value == null) throw invalid(path, "invalid namespaced key " + raw);
        return value;
    }

    private NamespacedKey key(String raw, String path) {
        NamespacedKey value = NamespacedKey.fromString(raw, plugin);
        if (value == null) throw invalid(path, "invalid namespaced key " + raw);
        return value;
    }

    private <E extends Enum<E>> Set<E> enumValues(List<String> values, Class<E> type, String path) {
        Set<E> result = new LinkedHashSet<>();
        for (String value : values) {
            try {
                result.add(Enum.valueOf(type, value.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException exception) {
                throw invalid(path, "unknown " + type.getSimpleName() + " " + value);
            }
        }
        return Set.copyOf(result);
    }

    private Set<String> lowerStrings(List<String> values) {
        return values.stream().map(value -> value.toLowerCase(Locale.ROOT)).collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    private Set<String> upperStrings(List<String> values) {
        return values.stream().map(value -> value.toUpperCase(Locale.ROOT)).collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    private ConfigurationSection requiredSection(ConfigurationSection parent, String key, String path) {
        ConfigurationSection section = parent.getConfigurationSection(key);
        if (section == null) throw invalid(path + "." + key, "section is required");
        return section;
    }

    private String requiredString(ConfigurationSection parent, String key, String path) {
        String value = parent.getString(key);
        if (value == null || value.isBlank()) throw invalid(path + "." + key, "value is required");
        return value;
    }

    private IllegalArgumentException invalid(String path, String message) {
        return new IllegalArgumentException(path + ": " + message);
    }
}
