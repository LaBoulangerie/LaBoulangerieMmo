package net.laboulangerie.laboulangeriemmo.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype;
import net.laboulangerie.laboulangeriemmo.api.xpboost.XpBoostObj;
import net.laboulangerie.laboulangeriemmo.core.blockus.Blockus;
import net.laboulangerie.laboulangeriemmo.core.blockus.redis.RedisBlockusHolder;
import net.laboulangerie.laboulangeriemmo.core.mapleaderboard.LeaderBoardManager;

public class MmoCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 0) return false;

        FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();

        if (args[0].equalsIgnoreCase("merge")) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    int i = 0;
                    Map<String, Blockus> blockuses = LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder().getBlockuses();
                    for (Iterator<Blockus> iterator = blockuses.values().iterator(); iterator.hasNext();) {
                        i++;
                        Blockus blockus = iterator.next();
                        LaBoulangerieMmo.PLUGIN.getBlockusHolder().addBlockus(blockus);
                        if (i == 5000) {
                            LaBoulangerieMmo.PLUGIN.getLogger().info("Migrated 5000 more blockuses!");
                            i=0;
                        }
                    }
                    
                    LaBoulangerieMmo.PLUGIN.getLogger().info("Completed merge successfully!");
                }
                
            }.runTaskAsynchronously(LaBoulangerieMmo.PLUGIN);
            return true;
        }
        if(args[0].equalsIgnoreCase("xpboost") && args.length >= 6) {
            if(args[1].equalsIgnoreCase("add")) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[2]);

                if(!(NumberUtils.isParsable(args[3]))) {
                    sender.sendMessage("§4Boost argument is not numeric");
                    return true;
                }

                if(!(NumberUtils.isParsable(args[5]))) {
                    sender.sendMessage("§4Time argument is not numeric");
                    return true;
                }

                Double boost = Double.parseDouble(args[3]);
                String identifier = args[4];
                int time = Integer.parseInt(args[5]);

                MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(offlinePlayer);
                TalentArchetype talentTarget = LaBoulangerieMmo.talentsRegistry.getTalent(identifier);

                XpBoostObj xpBoostObj = LaBoulangerieMmo.PLUGIN.getXpBoostManager().add(mmoPlayer, talentTarget, boost, time);

                List<TagResolver.Single> placeholders = Arrays.asList(
                        Placeholder.parsed("boost", xpBoostObj.getFormattedBoost()),
                        Placeholder.parsed("talent", talentTarget.displayName),
                        Placeholder.parsed("author", offlinePlayer.getName())
                );

                Component prefix = MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"));
                Component notification =  MiniMessage.miniMessage().deserialize(config.getString("lang.xp_boost.notif"),
                        TagResolver.resolver(placeholders));

                for(Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(prefix.append(notification));
                }
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("xp") && args.length >= 4) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(args[1]));
            if (player == null || LaBoulangerieMmo.PLUGIN.getMmoPlayerManager()
                    .getOfflinePlayer(player) == null) {
                sender.sendMessage("§4Impossible de récupérer le joueur: " + args[1]);
                return false;
            }
            MmoPlayer mmoPlayer =
                    LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(player);
            Talent talent = mmoPlayer.getTalent(args[3]);
            if (talent == null) {
                sender.sendMessage("§4" + args[1] + " n'as pas le talent " + args[3]);
                return false;
            }

            if (args[2].equalsIgnoreCase("see")) {
                sender.sendMessage(
                        "§a" + args[1] + ": §b" + args[3] + ": §rlvl §e" + talent.getLevel()
                                + "§r, lvl's xp §e" + (talent.getXp() - talent.getLevelXp())
                                + "§r, total xp §e" + talent.getXp());
                return true;
            }

            if (args.length < 5) return false;

            Double amount = null;
            try {
                amount = Double.parseDouble(args[4]);
            } catch (Exception e) {
                sender.sendMessage(
                        "§4L'argument §e" + args[4] + " §4n'est pas un nombre décimal valide.");
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
            sender.sendMessage("§bReloading config...");
            LaBoulangerieMmo.PLUGIN.saveDefaultConfig();
            LaBoulangerieMmo.PLUGIN.reloadConfig();
            sender.sendMessage("§bReloading abilities...");
            LaBoulangerieMmo.abilitiesRegistry.init();
            sender.sendMessage("§bReloading talents...");
            LaBoulangerieMmo.talentsRegistry.init();
            sender.sendMessage("§aReload complete");
            return true;
        }

        if (args[0].equalsIgnoreCase("blockus")) {
            if (args.length < 2) {
                sender.sendMessage(
                        "§bIl y a §e"
                                + LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockuses().size()
                                + " §bblockus dans le cache");
                sender.sendMessage(
                        "§bIl y a §e"
                                + LaBoulangerieMmo.PLUGIN.getBlockusHolder().getTotalBlockuses()
                                + " §bblockus au total");
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
                player.sendMessage(
                    LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) != null ? "§aLe bloc visé est un blockus"
                                : "§eLe bloc visé n'est pas un blockus");
                return true;
            }
            if (args[1].equalsIgnoreCase("mark")) {
                Blockus blockus = new Blockus(block);
                LaBoulangerieMmo.PLUGIN.getBlockusHolder()
                        .addBlockus(blockus);
                player.sendMessage("§aLe bloc visé est maintenant un blockus");
                return true;
            }
            if (args[1].equalsIgnoreCase("unmark")) {
                if (LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) == null) {
                    sender.sendMessage(
                            "§eLe bloc visé n'est pas un blockus impossible de le dé-marquer");
                    return true;
                }
                RedisBlockusHolder dataHolder =
                        LaBoulangerieMmo.PLUGIN.getBlockusHolder();
                dataHolder.removeBlockus(dataHolder.getBlockus(block));
                player.sendMessage("§aLe bloc visé n'est plus un blockus");
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("leaderboards")) {
            if (args.length > 1 && args[1].equalsIgnoreCase("freeAllMaps")) {
                try {
                    LeaderBoardManager.getInstance().freeAllMaps();
                } catch (IOException e) {
                    LaBoulangerieMmo.PLUGIN.getLogger()
                            .severe("Unable to free the maps :" + e.getMessage());
                }
                return true;
            }

            if (args.length > 3 && args[1].equalsIgnoreCase("updateMap")) {
                Integer id = 0;
                try {
                    id = Integer.valueOf(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(
                            "§4The id: " + args[2] + " cannot be converted to an integer!");
                    return true;
                }
                HashMap<String, Double> toSort = getLeaderBoardPretenders(sender, args[3]);
                if (toSort == null) return true;
                LeaderBoardManager.getInstance().updateMap(id, toSort);
                return true;
            }

            if (args.length > 2 && args[1].equalsIgnoreCase("create")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§4You need to be in game to use this command!");
                    return true;
                }
                Player player = (Player) sender;
                HashMap<String, Double> toSort = getLeaderBoardPretenders(sender, args[2]);
                if (toSort == null) return true;
                try {
                    LeaderBoardManager.getInstance().createLeaderBoard(toSort,
                            "           §16;Classement du metier " + args[2] + ":", "xp", 2, 2)
                            .stream().forEach(id -> player.getInventory()
                                    .addItem(LeaderBoardManager.getInstance().getMapItem(id)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command cmd,
            String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("xp", "reload", "rl", "blockus", "leaderboards", "xpboost");
        if (args[0].equalsIgnoreCase("xp")) {
            switch (args.length) {
                default:
                    return Arrays.asList("");
                case 2:
                    return null; // Lists players
                case 3:
                    return Arrays.asList("add", "subtract", "see", "set");
                case 4:
                    UUID uuid = Bukkit.getPlayerUniqueId(args[1]);
                    if (uuid == null) break;
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    MmoPlayer mmoPlayer =
                            LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(player);

                    return mmoPlayer.streamTalents().get().map(talent -> talent.getTalentId())
                            .collect(Collectors.toList());
                case 5:
                    if (args[2].equalsIgnoreCase("see")) return Arrays.asList("");
                    return Arrays.asList("10", "1000", "1000000");
            }
        }
        if (args[0].equalsIgnoreCase("blockus") && args.length == 2)
            return Arrays.asList("isBlockus", "mark", "unmark");
        if (args[0].equalsIgnoreCase("leaderboards") && args.length == 2)
            return Arrays.asList("updateMap", "freeAllMaps", "create");
        if (args.length == 3 && args[0].equalsIgnoreCase("leaderboards")
                && args[1].equalsIgnoreCase("create"))
            return Arrays.asList("mining", "thehunter", "woodcutting", "farmer");

        if (args[0].equalsIgnoreCase("xpboost")) {
            if (args.length == 2)
                return Arrays.asList("add");
            if (args[1].equalsIgnoreCase("add") && args.length > 2) {
                if (args.length == 3)
                    return null;
                if (args.length == 4)
                    return Arrays.asList("1.5", "2", "2.5", "3");
                if (args.length == 5) {
                    List<String> talentIdentifier = new ArrayList<>();
                    LaBoulangerieMmo.talentsRegistry.getTalents().forEach((key, value) -> {
                        talentIdentifier.add(value.identifier);
                    });
                    return talentIdentifier;
                }
                if (args.length == 6)
                    return Arrays.asList("60", "120", "180", "240");
            }
        }

        return Arrays.asList("");
    }

    private boolean isValidLeaderBoards(String name) {
        return Arrays.asList("mining", "thehunter", "woodcutting", "farmer").contains(name);
    }

    private HashMap<String, Double> getLeaderBoardPretenders(CommandSender sender,
            String leaderboardName) {
        if (!isValidLeaderBoards(leaderboardName)) {
            sender.sendMessage("§4Invalid leader board: " + leaderboardName);
            return null;
        }

        File folder = new File(LaBoulangerieMmo.PLUGIN.getDataFolder(), "players/");

        for (File file : folder.listFiles()) // Load all players
            LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(
                    Bukkit.getOfflinePlayer(UUID.fromString(file.getName().split(".json")[0])));

        return (HashMap<String, Double>) LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().stream()
                .collect(Collectors.toMap((p) -> p.getName(),
                        (p) -> p.getTalent(leaderboardName).getXp()));
    }
}
