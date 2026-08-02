package net.laboulangerie.laboulangeriemmo.commands;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

/** Gives one configured mastery item to the player running the command. */
public final class MasteryItemCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCette commande doit être exécutée en jeu.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage("§cUtilisation : /lbmmo masteryitem <item_id>");
            return true;
        }

        ItemStack item = LaBoulangerieMmo.PLUGIN.getRareLootManager().createItem(args[0]).orElse(null);
        if (item == null) {
            sender.sendMessage("§cItem de maîtrise inconnu ou indisponible : §e" + args[0]);
            return true;
        }

        give(player, item);
        sender.sendMessage("§aItem de maîtrise reçu : §e" + args[0]);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1) return List.of();
        return matching(LaBoulangerieMmo.PLUGIN.getRareLootManager().itemIds(), args[0]);
    }

    static void give(Player player, ItemStack item) {
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(item);
        overflow.values().forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }

    static List<String> matching(List<String> values, String prefix) {
        String normalized = prefix.toLowerCase(Locale.ROOT);
        return values.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(normalized)).toList();
    }
}
