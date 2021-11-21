package fr.laboulangerie.laboulangeriemmo.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class Stats implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.GOLD + "[LaBoulangerieMmo] " + "§4You must be in game to execute this command!");
            return false;
        }

        OfflinePlayer bukkitPlayer = (OfflinePlayer) sender;
        if(args.length > 0) {
            if (args.length > 1 && args[0].equalsIgnoreCase("leaderboard") && "miningwoodcuttingthehunterbaking".contains(args[1])){
            	File folder = new File(LaBoulangerieMmo.PLUGIN.getDataFolder(), "players/");
            	Double max = 0.0;
            	String name = null;
            	Double max2 = 0.0;
            	Double max3 = 0.0;
            	String name2 = null;
            	String name3 = null;

            	for(File file : folder.listFiles()){
            	    OfflinePlayer player2 = Bukkit.getOfflinePlayer(UUID.fromString(file.getName().split(".json")[0]));
            	    MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(player2);
            	    Talent talent = mmoPlayer.getTalent(args[1]);
            	    if (talent.getXp() > max) {
            	      max = talent.getXp();
            	      name = mmoPlayer.getName();
            	    }else if (talent.getXp() < max && talent.getXp() > max2) {
                        max2 = talent.getXp();
                        name2 = mmoPlayer.getName();
                	}else if (talent.getXp() < max2 && talent.getXp() > max3) {
                        max3 = talent.getXp();
                        name3 = mmoPlayer.getName();
                  	}
            	}
                if (name == null) {
                    sender.sendMessage("§cAucun joueur n'a d'exp en §a" + args[1]);
                }else {
                	sender.sendMessage("§lClassement de §a" + args[1] + "§r :");
            		sender.sendMessage("§e1. §a" + name + " " + max + "§r xp");
            		sender.sendMessage("§62. §a" + name2 + " " + max2 + "§r xp");
            		sender.sendMessage("§c3. §a" + name3 + " " + max3 + "§r xp");
                }
                return true;
            }
            if (!sender.hasPermission("laboulangeriemmo.stats.see")) {
                sender.sendMessage(ChatColor.GOLD + "[LaBoulangerieMmo] " + "§4Vous n'avez pas la permission de voir les statistiques d'un autre joueur.");
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
            String firstPart = "§b"+talent.getDisplayName();
            String secondPart = "§r: lvl §e"+talent.getLevel(0.2);
            String thirdPart = "§r, xp §e" + (talent.getXp() - talent.getLevelXp(0.2)) +"§5/§e"+((int) Math.pow((talent.getLevel(0.2)+1) / 0.2, 2)-talent.getLevelXp(0.2));
            target.sendMessage(firstPart + "" + secondPart + "" + thirdPart);
        });
    }
}
