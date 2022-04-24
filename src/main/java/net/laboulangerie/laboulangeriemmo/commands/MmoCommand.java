package net.laboulangerie.laboulangeriemmo.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.blockus.Blockus;
import net.laboulangerie.laboulangeriemmo.blockus.BlockusDataHolder;
import net.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class MmoCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args) {
        if (args.length == 0)
            return false;

        if (args[0].equalsIgnoreCase("xp") && args.length >= 4) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(args[1]));
            if (player == null || LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(player) == null) {
                sender.sendMessage("§4Impossible de récupérer le joueur: " + args[1]);
                return false;
            }
            MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(player);
            Talent talent = mmoPlayer.getTalent(args[3]);
            if (talent == null) {
                sender.sendMessage("§4" + args[1] + " n'as pas le talent " + args[3]);
                return false;
            }

            if (args[2].equalsIgnoreCase("see")) {
                sender.sendMessage(
                        "§a" + args[1] + ": §b"
                                + args[3] + ": §rlvl §e" + talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER)
                                + "§r, xp §e" + (talent.getXp() - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER))
                                + "§r, total xp §e" + talent.getXp()
                                + "§r/§e" + talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER));
                return true;
            }

            if (args.length < 5)
                return false;

            Double amount = null;
            try {
                amount = Double.parseDouble(args[4]);
            } catch (Exception e) {
                sender.sendMessage("§4L'argument §e" + args[4] + " §4n'est pas un nombre décimal valide.");
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
            sender.sendMessage("§bWriting blockuses...");
            try {
                LaBoulangerieMmo.PLUGIN.getBlockusDataManager().writeBlockuses();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sender.sendMessage("§bReloading config");
            LaBoulangerieMmo.PLUGIN.reloadConfig();
            sender.sendMessage("§aReload complete");
            return true;
        }

        if (args[0].equalsIgnoreCase("blockus")) {
            if (args.length < 2) {
                sender.sendMessage("§bIl y a §e"
                        + LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder().getBlockuses().size()
                        + " §bblockus");
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("§4Vous devez être en jeu pour exécuter la commande");
                return true;
            }
            Player player = (Player) sender;
            RayTraceResult result = player.rayTraceBlocks(5);
            if (result == null) {
                player.sendMessage("§4Vous ne regardez pas un bloc dans un rayon de 5 blocs");
                return true;
            }
            Block block = result.getHitBlock();

            if (args[1].equalsIgnoreCase("isBlockus")) {
                player.sendMessage(block.hasMetadata("laboulangerie:placed") ? "§aLe bloc visé est un blockus"
                        : "§eLe bloc visé n'est pas un blockus");
                return true;
            }
            if (args[1].equalsIgnoreCase("mark")) {
                Blockus blockus = new Blockus(block);
                blockus.putMetadata("laboulangerie:placed", player.getUniqueId());
                blockus.markAsBlockus();
                LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder().addBlockus(blockus);
                player.sendMessage("§aLe bloc visé est maintenant un blockus");
                return true;
            }
            if (args[1].equalsIgnoreCase("unmark")) {
                if (!block.hasMetadata("laboulangerie:placed")) {
                    sender.sendMessage("§eLe bloc visé n'est pas un blockus impossible de le dé-marquer");
                    return true;
                }
                block.removeMetadata("laboulangerie:placed", LaBoulangerieMmo.PLUGIN);
                BlockusDataHolder dataHolder = LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder();
                dataHolder.removeBlockus(dataHolder.getBlockus(block));
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1)
            return Arrays.asList("xp", "reload", "rl", "blockus");
        if (args[0].equalsIgnoreCase("xp")) {
            switch (args.length) {
                default:
                case 2:
                    return null; // Lists players
                case 3:
                    return Arrays.asList("add", "subtract", "see", "set");
                case 4:
                    UUID uuid = Bukkit.getPlayerUniqueId(args[1]);
                    if (uuid == null) break;
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(player);

                    return mmoPlayer.streamTalents().get().map(talent -> talent.getTalentId())
                            .collect(Collectors.toList());
                case 5:
                    if (args[2].equalsIgnoreCase("see"))
                        return Arrays.asList("");
                    return Arrays.asList("10", "100", "1000");
            }
        }
        if (args[0].equalsIgnoreCase("blockus") && args.length == 2)
            return Arrays.asList("isBlockus", "mark", "unmark");
        return null;
    }
}
