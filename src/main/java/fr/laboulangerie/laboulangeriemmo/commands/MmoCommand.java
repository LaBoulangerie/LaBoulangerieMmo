package fr.laboulangerie.laboulangeriemmo.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class MmoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0) return false;

        if (args[0].equalsIgnoreCase("xp") && args.length >= 4) {
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null || !player.isOnline() || LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player) == null) {
                sender.sendMessage("§4Impossible de récupérer le joueur: "+args[1]);
                return false;
            }
            MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);
            Talent talent = mmoPlayer.getTalent(args[3]);

            if (talent == null) {
                sender.sendMessage("§4"+args[1]+" n'as pas le talent "+args[3]);
                return false;
            }

            if (args[2].equalsIgnoreCase("see")) {
                sender.sendMessage(
                    "§a" + args[1] + ": §b"
                    + args[3] + ": §rlvl §e"+talent.getLevel(0.2)
                    + "§r, xp §e" + ((talent.getXp() * 100_000 - talent.getLevelXp(0.2) * 100_000) / 100_000)
                );
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
                sender.sendMessage("§aVous avez ajouté §e" + args[4] + "§exp au talent §e"
                 + talent.getDisplayName() + "§a de §e" + args[1]);
                return true;
            }

            if (args[2].equalsIgnoreCase("substract")) {
                talent.decrementXp(amount);
                sender.sendMessage("§aVous avez retiré §e" + args[4] + "§exp au talent §e"
                 + talent.getDisplayName() + "§a de §e" + args[1]);
                return true;
            }
        }
        return false;
    }
}
