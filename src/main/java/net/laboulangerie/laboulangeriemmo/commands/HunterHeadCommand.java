package net.laboulangerie.laboulangeriemmo.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.HeadConfig;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.MobHeadsRegistry;

/** Gives one exact Hunter trophy head to the player running the command. */
public final class HunterHeadCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCette commande doit être exécutée en jeu.");
            return true;
        }
        if (args.length < 1 || args.length > 2) {
            usage(sender);
            return true;
        }

        int headCount = MobHeadsRegistry.getConfiguredHeadCount(args[0]);
        if (headCount == 0) {
            sender.sendMessage("§cMob sans tête Hunter configurée : §e" + args[0]);
            return true;
        }
        if (headCount > 1 && args.length == 1) {
            sender.sendMessage("§cCe mob possède plusieurs têtes ; indiquez une variante.");
            usage(sender);
            return true;
        }

        String variant = args.length == 2 ? args[1] : null;
        HeadConfig head = MobHeadsRegistry.getConfiguredHead(args[0], variant);
        if (head == null) {
            sender.sendMessage("§cVariante de tête inconnue : §e" + variant);
            sender.sendMessage("§7Variantes valides : §f"
                    + String.join(", ", MobHeadsRegistry.getVariants(args[0])));
            return true;
        }

        MasteryItemCommand.give(player, head.createHead());
        sender.sendMessage("§aTête Hunter reçue : §e" + args[0]
                + (variant == null ? "" : " §7(" + variant + ")"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return MasteryItemCommand.matching(MobHeadsRegistry.getMobTypes(), args[0]);
        }
        if (args.length == 2) {
            return MasteryItemCommand.matching(MobHeadsRegistry.getVariants(args[0]), args[1]);
        }
        return List.of();
    }

    private static void usage(CommandSender sender) {
        sender.sendMessage("§cUtilisation : /lbmmo hunterhead <mob_type> [variante]");
        sender.sendMessage("§7La variante est obligatoire si le mob possède plusieurs têtes ; utilisez DEFAULT pour la tête normale.");
    }
}
