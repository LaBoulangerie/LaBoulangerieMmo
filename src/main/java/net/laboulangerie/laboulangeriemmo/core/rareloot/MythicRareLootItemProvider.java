package net.laboulangerie.laboulangeriemmo.core.rareloot;

import java.lang.reflect.Method;
import java.util.Optional;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.rareloot.RareLootItemProvider;
import org.bukkit.inventory.ItemStack;

/** Reflection keeps this optional hook tolerant of supported MythicMobs API revisions. */
public final class MythicRareLootItemProvider implements RareLootItemProvider {
    @Override
    public String id() {
        return "mythicmobs";
    }

    @Override
    public ItemStack createItem(String itemId) {
        if (!LaBoulangerieMmo.MYTHICMOBS_SUPPORT) return null;
        try {
            Class<?> mythicBukkit = Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            Object instance = mythicBukkit.getMethod("inst").invoke(null);
            Object manager = instance.getClass().getMethod("getItemManager").invoke(instance);
            Method getter = manager.getClass().getMethod("getItemStack", String.class);
            Object result = getter.invoke(manager, itemId);
            if (result instanceof Optional<?> optional) result = optional.orElse(null);
            return result instanceof ItemStack item ? item.clone() : null;
        } catch (ReflectiveOperationException | LinkageError exception) {
            LaBoulangerieMmo.PLUGIN.getLogger().warning(
                    "Unable to resolve MythicMobs item '" + itemId + "': " + exception.getMessage());
            return null;
        }
    }
}
