package fr.laboulangerie.laboulangeriemmo.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import net.kyori.adventure.text.Component;

public class Stats implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4You must be in game to execute this command!");
            return false;
        }

        OfflinePlayer bukkitPlayer = (OfflinePlayer) sender;
        if(args.length > 0) {
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
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("stats-"+source.getName(), "dummy", Component.text("Stats"));
        
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score levelSection = objective.getScore("§aExperience:");
        levelSection.setScore(1);

        source.streamTalents().get().forEach(talent -> {
            Score xpScore = objective.getScore(
                "§b"+talent.getDisplayName()
                +"§r: lvl §e"+talent.getLevel(0.2)
                +"§r, xp §e" + ((talent.getXp() * 100_000 - talent.getLevelXp(0.2) * 100_000) / 100_000)
            );
            xpScore.setScore(0);
        });

        target.setScoreboard(board);

        new BukkitRunnable(){
            @Override
            public void run() {
                target.setScoreboard(manager.getNewScoreboard());
            }
        }.runTaskLater(LaBoulangerieMmo.PLUGIN, 200);
    }
}
