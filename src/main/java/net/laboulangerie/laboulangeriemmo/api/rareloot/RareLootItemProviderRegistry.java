package net.laboulangerie.laboulangeriemmo.api.rareloot;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

public final class RareLootItemProviderRegistry {
    private final Map<String, RareLootItemProvider> providers = new LinkedHashMap<>();

    public void register(RareLootItemProvider provider) {
        if (provider == null || provider.id() == null || provider.id().isBlank()) {
            throw new IllegalArgumentException("A rare-loot item provider must have an id");
        }
        String id = provider.id().toLowerCase(Locale.ROOT);
        if (providers.putIfAbsent(id, provider) != null) {
            throw new IllegalArgumentException("Rare-loot item provider already registered: " + id);
        }
    }

    public boolean contains(String providerId) {
        return providerId != null && providers.containsKey(providerId.toLowerCase(Locale.ROOT));
    }

    public Optional<ItemStack> create(String providerId, String itemId) {
        RareLootItemProvider provider = providers.get(providerId.toLowerCase(Locale.ROOT));
        if (provider == null) return Optional.empty();
        ItemStack item = provider.createItem(itemId);
        return item == null ? Optional.empty() : Optional.of(item.clone());
    }
}
