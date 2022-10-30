package net.laboulangerie.laboulangeriemmo.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;

public class Stats implements TabExecutor {
    /**
     * per talent list of players classed by their xp
     * cleared every 2 minutes
     */
    private static Map<String, List<MmoPlayer>> talentTopCache = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        OfflinePlayer source = null;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("leaderboard")) {
                if (args.length == 1) return false;
                if (LaBoulangerieMmo.talentsRegistry.getTalent(args[1]) == null && args[1] == "total") {
                    sender.sendMessage("§4Invalid talent.");
                }
                int page = 0;
                if (args.length > 2) {
                    try {
                        page = Integer.parseInt(args[2])-1;
                        if (page < 0) {
                            sender.sendMessage("§4Invalid page number");
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§4Invalid page number");
                        return true;
                    }
                }
                if (talentTopCache.get(args[1]) == null) {
                    File folder = new File(LaBoulangerieMmo.PLUGIN.getDataFolder(), "players/");

                    if(args[1].equals("total")) {
                    	talentTopCache.put(args[1], List.of(folder.listFiles()).stream().map(file ->
                        	LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(Bukkit.getOfflinePlayer(UUID.fromString(file.getName().split(".json")[0])))
                    			).sorted((v1, v2) ->  (v2.getPalier()).compareTo(v1.getPalier())).collect(Collectors.toList()));
                    }
                    else {
                    	talentTopCache.put(args[1], List.of(folder.listFiles()).stream().map(file ->
                    	LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(Bukkit.getOfflinePlayer(UUID.fromString(file.getName().split(".json")[0])))
                			).sorted((v1, v2) ->  ((Double) v2.getTalent(args[1]).getXp()).compareTo(v1.getTalent(args[1]).getXp())).collect(Collectors.toList()));
                    }
                    scheduleCacheClear(args[1]);
                }
                List<MmoPlayer> orderedPlayers = talentTopCache.get(args[1]);

                if(args[1].equals("total")) {
                	sender.sendMessage("§3----------§8[Page §7" + (page+1) + "§8]§3----------");
                	for (int i = page*10; i < (orderedPlayers.size() < (page+1) *10 ? orderedPlayers.size() : (page+1) *10); i++) {
                    	final MmoPlayer player = orderedPlayers.get(i);
                    	sender.sendMessage("§e" + (i+1) + ". §a" + player.getName() + " §6- §3total §9" + player.getPalier());
                	}
                }
                else {
                	sender.sendMessage("§3----------§8[Page §7" + (page+1) + "§8]§3----------");
                	for (int i = page*10; i < (orderedPlayers.size() < (page+1) *10 ? orderedPlayers.size() : (page+1) *10); i++) {
                    	final MmoPlayer player = orderedPlayers.get(i);
                    	sender.sendMessage("§e" + (i+1) + ". §a" + player.getName() + " §6- §3level §9" + player.getTalent(args[1]).getLevel());
                	}
                }

                return true;
            }

            if (!sender.hasPermission("laboulangeriemmo.stats.see")) {
                sender.sendMessage(ChatColor.GOLD + "[LaBoulangerieMmo] §4You don't have the permission to see other's stats.");
                return true;
            }
            source = Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(args[0]));
            if (source == null) {
                sender.sendMessage("§4Invalid player!");
                return true;
            }
        }
        if (source == null && !(sender instanceof Player)) {
            sender.sendMessage("§4You must be in game to use this command!");
            return true;
        } else if (source == null) source = (OfflinePlayer) sender;

        MmoPlayer mmoSource = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(source);
        sendStatsTo(sender, mmoSource);
        return true;
    }

    private void sendStatsTo(CommandSender target, MmoPlayer source) {

        target.sendMessage("");
        target.sendMessage("§8------§6Stats§8------");
        target.sendMessage("§aExperience :");

        source.streamTalents().get().forEach(talent -> {
            String firstPart = "§b" + talent.getDisplayName();
            String secondPart = "§r: lvl §e" + talent.getLevel();
            String thirdPart = "§r, xp §e" + LaBoulangerieMmo.formatter.format(talent.getXp() - talent.getLevelXp())
                    + "§5/§e" + LaBoulangerieMmo.formatter.format(talent.getXpToNextLevel());
            target.sendMessage(firstPart + "" + secondPart + "" + thirdPart);
        });
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("leaderboard");
            if (sender.hasPermission("laboulangeriemmo.stats.see"))
                suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList()));
        }else if (args[0].equalsIgnoreCase("leaderboard")) {
            switch (args.length) {
                case 2:
                     suggestions = new ArrayList<>(LaBoulangerieMmo.talentsRegistry.getTalents().keySet());
                     suggestions.add("total");
                break;
                case 3:
                    suggestions = Arrays.asList("1", "2", "3", "4", "5");
                break;
                default:
            }
        }
        return suggestions.stream().filter(str -> str.startsWith(args[args.length == 0 ? 0 : args.length-1]))
            .collect(Collectors.toList());
    }

    /**
     * Clear the entry for the specified talent in {@code talentTopCache} in 1 minute
     * @param talent
     */
    private void scheduleCacheClear(String talent) {
        new BukkitRunnable() {
            @Override
            public void run() {
                talentTopCache.remove(talent);
            }
        }.runTaskLater(LaBoulangerieMmo.PLUGIN, 20*60);
    }
}
