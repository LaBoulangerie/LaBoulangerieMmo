package net.laboulangerie.laboulangeriemmo.core.rareloot;

import java.util.LinkedHashMap;
import java.util.Map;
import net.laboulangerie.laboulangeriemmo.api.rareloot.RareLootItemProvider;
import org.bukkit.inventory.ItemStack;

public final class NativeRareLootItemProvider implements RareLootItemProvider {
    private volatile Map<String, ItemStack> templates = Map.of();

    @Override
    public String id() {
        return "native";
    }

    public void replaceTemplates(Map<String, ItemStack> templates) {
        Map<String, ItemStack> copies = new LinkedHashMap<>();
        templates.forEach((id, item) -> copies.put(id, item.clone()));
        this.templates = Map.copyOf(copies);
    }

    @Override
    public ItemStack createItem(String itemId) {
        ItemStack template = templates.get(itemId);
        return template == null ? null : template.clone();
    }
}
