package net.laboulangerie.laboulangeriemmo.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class Stats implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args) {
        OfflinePlayer bukkitPlayer = (OfflinePlayer) sender;
        if (args.length > 0) {
            if (args.length > 1 && args[0].equalsIgnoreCase("leaderboard")
                    && "miningwoodcuttingthehunterbaking".contains(args[1])) {
                File folder = new File(LaBoulangerieMmo.PLUGIN.getDataFolder(), "players/");
                Integer maxLevel = 0;
                Integer max2Level = 0;
                Integer max3Level = 0;
                Double maxExp = 0.0;
                Double max2Exp = 0.0;
                Double max3Exp = 0.0;
                String name = null;
                String name2 = null;
                String name3 = null;

                for (File file : folder.listFiles()) {
                    OfflinePlayer player2 = Bukkit.getOfflinePlayer(UUID.fromString(file.getName().split(".json")[0]));
                    MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(player2);
                    Talent talent = mmoPlayer.getTalent(args[1]);
                    if (talent.getXp() > maxExp && talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) >= maxLevel) {
                        maxLevel = talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
                        maxExp = talent.getXp();
                        name = mmoPlayer.getName();
                    } else if (talent.getXp() < maxExp && talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) == maxLevel) {
                        max2Level = talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
                        name2 = mmoPlayer.getName();
                        max2Exp = talent.getXp();
                    } else if (talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) < maxLevel && talent.getXp() > max2Exp
                            && talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) >= max2Level) {
                        max2Level = talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
                        name2 = mmoPlayer.getName();
                        max2Exp = talent.getXp();
                    } else if (talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) < maxLevel && talent.getXp() < max2Exp
                            && talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) == max2Level) {
                        max3Level = talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
                        name3 = mmoPlayer.getName();
                        max3Exp = talent.getXp();
                    } else if (talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) < max2Level && talent.getXp() > max3Exp
                            && talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) >= max3Level) {
                        max3Level = talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
                        name3 = mmoPlayer.getName();
                        max3Exp = talent.getXp();
                    }
                }
                if (name == null) {
                    sender.sendMessage("§cAucun joueur n'a d'exp en §a" + args[1]);
                } else {
                    sender.sendMessage("§lClassement de §a" + args[1] + "§r :");
                    sender.sendMessage("§e1. §a" + name + " qui est niveau" + maxLevel + " avec " + maxExp + "§r xp");
                    sender.sendMessage(
                            "§62. §a" + name2 + " qui est niveau" + max2Level + " avec " + max2Exp + "§r xp");
                    sender.sendMessage(
                            "§c3. §a" + name3 + " qui est niveau" + max3Level + " avec " + max3Exp + "§r xp");
                }
                return true;
            }
            if (!sender.hasPermission("laboulangeriemmo.stats.see")) {
                sender.sendMessage(ChatColor.GOLD + "[LaBoulangerieMmo] "
                        + "§4Vous n'avez pas la permission de voir les statistiques d'un autre joueur.");
                return true;
            }
            bukkitPlayer = Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(args[0]));
            if (bukkitPlayer == null) {
                sender.sendMessage("§4Invalid player!");
                return false;
            }
        }
        MmoPlayer player = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(bukkitPlayer);
        sendStatsTo((Player) sender, player);
        return true;
    }

    private void sendStatsTo(Player target, MmoPlayer source) {

        target.sendMessage("");
        target.sendMessage("§8------§6Stats§8------");
        target.sendMessage("§aExperience :");

        source.streamTalents().get().forEach(talent -> {
            String firstPart = "§b" + talent.getDisplayName();
            String secondPart = "§r: lvl §e" + talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
            String thirdPart = "§r, xp §e" + (talent.getXp() - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER))
                    + "§5/§e" + talent.getXpToNextLevel(LaBoulangerieMmo.XP_MULTIPLIER);
            target.sendMessage(firstPart + "" + secondPart + "" + thirdPart);
        });
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<String>();
            list.add("leaderboard");
            if (sender.hasPermission("laboulangeriemmo.stats.see"))
                list.addAll(Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList()));
            return list;
        }
        if (args[0].equalsIgnoreCase("leaderboard")) {
            switch (args.length) {
                case 2:
                    return Arrays.asList("mining", "woodcutting", "thehunter", "baking"); // Lists players
                default:
                    return Arrays.asList("");
            }
        }
        return null;
    }
}
