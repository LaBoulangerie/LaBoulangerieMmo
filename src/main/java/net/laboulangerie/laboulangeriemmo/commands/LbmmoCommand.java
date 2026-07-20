package net.laboulangerie.laboulangeriemmo.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.commands.talenttree.TalentTree;

/** Routes all MMO commands through the common /lbmmo root. */
public class LbmmoCommand implements CommandExecutor, TabCompleter {
    private static final String ADMIN_PERMISSION = "laboulangeriemmo.mmo";
    private final Map<String, RoutedCommand> routes = new LinkedHashMap<>();

    public LbmmoCommand() {
        MmoCommand admin = new MmoCommand();
        register("stats", new Stats(), "laboulangeriemmo.stats", true);
        register("combo", new Combo(), "laboulangeriemmo.combo", true);
        register("talent", new TalentTree(), "laboulangeriemmo.talent", true);
        if (LaBoulangerieMmo.PLUGIN.getServer().getPluginManager().getPlugin("Towny") != null) {
            register("towny", new TownyMmo(), null, true);
        }
        for (String name : Arrays.asList("xp", "xpboost", "reload", "rl", "blockus", "leaderboards", "merge")) {
            register(name, admin, ADMIN_PERMISSION, false);
        }
    }

    private void register(String name, CommandExecutor executor, String permission, boolean stripFirstArgument) {
        routes.put(name, new RoutedCommand(executor, executor instanceof TabCompleter ? (TabCompleter) executor : null,
                permission, stripFirstArgument));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (args.length == 0) return false;
        RoutedCommand route = routes.get(args[0].toLowerCase(Locale.ROOT));
        if (route == null) return false;
        if (route.permission != null && !sender.hasPermission(route.permission)) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }
        String[] routedArgs = route.stripFirstArgument ? Arrays.copyOfRange(args, 1, args.length) : args;
        return route.executor.onCommand(sender, command, label, routedArgs);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            List<String> suggestions = new ArrayList<>();
            routes.forEach((name, route) -> {
                if (name.startsWith(prefix) && (route.permission == null || sender.hasPermission(route.permission))) {
                    suggestions.add(name);
                }
            });
            return suggestions;
        }
        RoutedCommand route = routes.get(args[0].toLowerCase(Locale.ROOT));
        if (route == null || route.tabCompleter == null
                || (route.permission != null && !sender.hasPermission(route.permission))) return List.of();
        String[] routedArgs = route.stripFirstArgument ? Arrays.copyOfRange(args, 1, args.length) : args;
        return route.tabCompleter.onTabComplete(sender, command, alias, routedArgs);
    }

    private record RoutedCommand(CommandExecutor executor, TabCompleter tabCompleter, String permission,
            boolean stripFirstArgument) {}
}
