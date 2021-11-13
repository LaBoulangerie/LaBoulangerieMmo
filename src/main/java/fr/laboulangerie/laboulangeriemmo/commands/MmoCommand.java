package fr.laboulangerie.laboulangeriemmo.commands;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class MmoCommand implements CommandExecutor, TabCompleter {
	
	private File playersFolder;
    private LaBoulangerieMmo laBoulangerieMmo;

	
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0) return false;

        if (args[0].equalsIgnoreCase("xp") && args.length >= 4) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(args[1]));
            if (player == null || LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(player) == null) {
                sender.sendMessage("§4Impossible de récupérer le joueur: "+args[1]);
                return false;
            }
            MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(player);
            Talent talent = mmoPlayer.getTalent(args[3]);
            if (talent == null) {
                sender.sendMessage("§4"+args[1]+" n'as pas le talent "+args[3]);
                return false;
            }

            if (args[2].equalsIgnoreCase("see")) {
                sender.sendMessage(
                	"§a" + args[1] + ": §b"
                    + args[3] + ": §rlvl §e"+talent.getLevel(0.2)
                    + "§r, xp §e" + (talent.getXp() - talent.getLevelXp(0.2))
                    + "§r, total xp §e"+ talent.getXp()
                    + " " + talent.getLevelXp(0.2)
                );
                return true;
            }
            
            if (args[2].equalsIgnoreCase("leaderboard")) {
            	this.playersFolder = new File(laBoulangerieMmo.getDataFolder(), "players/");
            	Double max = 0.0;
            	String name = null;

            	for(File file : playersFolder.listFiles()){
            	    OfflinePlayer player2 = Bukkit.getOfflinePlayer(UUID.fromString(file.getName()));
            	    MmoPlayer mmoPlayer2 = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(player2);
            	    Talent talent2 = mmoPlayer2.getTalent(args[3]);
            	    if (talent2.getXp() > max) {
            	      max = talent.getXp();
            	      name = mmoPlayer2.getName();
            	    }
            	}
                if (name == null) {
                    sender.sendMessage("§cAucun joueur n'a d'exp en §a" + args[3]);
                }else {
                sender.sendMessage("§rLe meilleur joueur en §a" + args[3] + "§r est §a" + name + "§r car il a §e" + max + "§r d'exp");
                }
                return true;
            }

            if (args.length < 5) return false;

            Double amount = null;
            try {
                amount =  Double.parseDouble(args[4]);
            } catch (Exception e) {
                sender.sendMessage("§4L'argument §e"+ args[4] + " §4n'est pas un nombre décimal valide.");
                return true;
            }

            if (args[2].equalsIgnoreCase("add")) {
                talent.incrementXp(amount);
                sender.sendMessage("§aVous avez ajouté §e" + args[4] + "§axp au talent §e"
                 + talent.getDisplayName() + "§a de §e" + args[1]);
                return true;
            }

            if (args[2].equalsIgnoreCase("subtract")) {
                talent.decrementXp(amount);
                sender.sendMessage("§aVous avez retiré §e" + args[4] + "§axp au talent §e"
                 + talent.getDisplayName() + "§a de §e" + args[1]);
                return true;
            }

            if (args[2].equalsIgnoreCase("set")) {
                talent.setXp(amount);
                sender.sendMessage("§aVous avez mis à §e" + args[4] + "§axp le talent §e"
                 + talent.getDisplayName() + "§a de §e" + args[1]);
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
            LaBoulangerieMmo.PLUGIN.reloadConfig();
            sender.sendMessage("§aReload complete");
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return Arrays.asList("xp", "reload", "rl");
        if (args[0].equalsIgnoreCase("xp")) {
            switch (args.length) {
                default:
                case 2:
                    return null; // Lists players
                case 3:
                    return Arrays.asList("add", "subtract", "see", "set", "leaderboard");
                case 4:
                    OfflinePlayer player = Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(args[1]));
                    MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(player);
                    
                    return mmoPlayer.streamTalents().get().map(talent -> talent.getTalentId()).collect(Collectors.toList());
                case 5:
                    if (args[2].equalsIgnoreCase("see")) return Arrays.asList("");
                    return Arrays.asList("10", "100", "1000");
            }
        }
        return null;
    }
}
