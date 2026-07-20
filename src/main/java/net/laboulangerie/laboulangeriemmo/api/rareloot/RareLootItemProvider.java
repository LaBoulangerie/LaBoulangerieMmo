package net.laboulangerie.laboulangeriemmo.api.rareloot;

import org.bukkit.inventory.ItemStack;

/** Supplies custom items referenced by rare-loot configuration files. */
public interface RareLootItemProvider {
    String id();

    ItemStack createItem(String itemId);
}
