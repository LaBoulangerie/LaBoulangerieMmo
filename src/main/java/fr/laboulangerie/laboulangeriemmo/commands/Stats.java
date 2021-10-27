package fr.laboulangerie.laboulangeriemmo.commands;

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

public class Stats implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.GOLD + "[LaBoulangerieMmo] " + "§4You must be in game to execute this command!");
            return false;
        }

        OfflinePlayer bukkitPlayer = (OfflinePlayer) sender;
        if(args.length > 0) {
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
            String thirdPart = "§r, xp §e" + (talent.getXp() - talent.getLevelXp(0.2)) +"§5/§e"+(int) Math.pow((talent.getLevel(0.2)+1) / 0.2, 2);
            target.sendMessage(firstPart + "" + secondPart + "" + thirdPart);
        });
    }
}
