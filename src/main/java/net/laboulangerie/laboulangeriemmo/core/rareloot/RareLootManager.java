package net.laboulangerie.laboulangeriemmo.core.rareloot;

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.rareloot.RareLootItemProviderRegistry;
import net.laboulangerie.laboulangeriemmo.api.rareloot.RegisterRareLootItemProvidersEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;

public final class RareLootManager {
    public record ReloadResult(boolean success, String message, List<String> warnings) {}

    private static final String[] DEFAULT_RESOURCES = {
        "rare-loots/settings.yml", "rare-loots/items.yml", "rare-loots/chances.yml",
        "rare-loots/jobs/hunter.yml", "rare-loots/jobs/lumberjack.yml", "rare-loots/jobs/farmer.yml",
        "rare-loots/jobs/miner.yml"
    };

    private final LaBoulangerieMmo plugin;
    private final RareLootItemProviderRegistry providers = new RareLootItemProviderRegistry();
    private final NativeRareLootItemProvider nativeProvider = new NativeRareLootItemProvider();
    private final RareLootEngine engine;

    public RareLootManager(LaBoulangerieMmo plugin) {
        this.plugin = plugin;
        providers.register(nativeProvider);
        if (LaBoulangerieMmo.MYTHICMOBS_SUPPORT) providers.register(new MythicRareLootItemProvider());
        Bukkit.getPluginManager().callEvent(new RegisterRareLootItemProvidersEvent(providers));
        engine = new RareLootEngine(providers);
    }

    public void createDefaults() {
        for (String resource : DEFAULT_RESOURCES) {
            File target = new File(plugin.getDataFolder(), resource);
            if (!target.exists()) plugin.saveResource(resource, false);
        }
    }

    public ReloadResult reload() {
        try {
            RareLootConfigLoader.Result result = new RareLootConfigLoader(plugin, providers).load();
            nativeProvider.replaceTemplates(result.nativeItems());
            engine.replaceRegistry(result.registry());
            String message = "Rare loots: " + result.registry().items().size() + " items, "
                    + result.registry().chanceProfiles().size() + " profiles, " + result.registry().ruleCount()
                    + " rules loaded";
            result.warnings().forEach(warning -> plugin.getLogger().warning("Rare loots: " + warning));
            return new ReloadResult(true, message, result.warnings());
        } catch (IOException | InvalidConfigurationException | IllegalArgumentException exception) {
            String message = "Rare-loot reload failed; previous configuration kept: " + exception.getMessage();
            plugin.getLogger().severe(message);
            return new ReloadResult(false, message, List.of());
        }
    }

    public RareLootEngine engine() {
        return engine;
    }

    public RareLootItemProviderRegistry providers() {
        return providers;
    }
}
