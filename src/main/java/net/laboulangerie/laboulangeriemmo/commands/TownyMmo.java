package net.laboulangerie.laboulangeriemmo.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;

public class TownyMmo implements CommandExecutor, TabCompleter{

    private static Map<String, List<Town>> townTopCache = new HashMap<>();
    private static Map<String, List<Nation>> nationTopCache = new HashMap<>();


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<String>();
        if (args.length == 1) {
            list = Arrays.asList("town", "nation");
        }
        if (args.length == 2) {
            list = new ArrayList<>(LaBoulangerieMmo.talentsRegistry.getTalents().keySet());
            list.add("total ");
        }
        if (args.length == 3) {
            list = Arrays.asList("1", "2", "3", "4", "5");
        }
        return list.stream().filter(str -> str.startsWith(args[args.length == 0 ? 0 : args.length-1]))
            .collect(Collectors.toList());

    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args[0].equalsIgnoreCase("town")) {
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
            if (townTopCache.get(args[1]) == null) {
                if(args[1].equals("total")) {
                	townTopCache.put(args[1], List.of(TownyUniverse.getInstance().getTowns()).stream().map(town ->
                	TownyUniverse.getInstance().getTown(town.toArray()[0].toString())
            			).sorted((v1, v2) ->  (MmoPlayer.getTownTotalLevel(v2)).compareTo(MmoPlayer.getTownTotalLevel(v1))).collect(Collectors.toList()));
                }
                else {
                	townTopCache.put(args[1], List.of(TownyUniverse.getInstance().getTowns()).stream().map(town ->
                	TownyUniverse.getInstance().getTown(town.toArray()[0].toString())
            			).sorted((v1, v2) ->  (MmoPlayer.getTownTalentLevel(v2, args[1])).compareTo(MmoPlayer.getTownTalentLevel(v1, args[1]))).collect(Collectors.toList()));
                }
                scheduleCacheClear(args[1]);
            }
            final List<Town> orderedTowns = townTopCache.get(args[1]);

            if(args[1].equals("total")) {
            	sender.sendMessage("§3----------§8[Page §7" + (page+1) + "§8]§3----------");
            	for (int i = page*10; i < (orderedTowns.size() < (page+1) *10 ? orderedTowns.size() : (page+1) *10); i++) {
                	Town town = orderedTowns.get(i);
                	sender.sendMessage("§e" + (i+1) + ". §a" + town.getName() + " §6- §3total §9" + MmoPlayer.getTownTotalLevel(town));
            	}
            }
            else {
            	sender.sendMessage("§3----------§8[Page §7" + (page+1) + "§8]§3----------");
            	for (int i = page*10; i < (orderedTowns.size() < (page+1) *10 ? orderedTowns.size() : (page+1) *10); i++) {
                	final Town town = orderedTowns.get(i);
                	sender.sendMessage("§e" + (i+1) + ". §a" + town.getName() + " §6- §3level §9" + MmoPlayer.getTownTalentLevel(town, args[1]));
            	}
            }
        }
        if (args[0].equalsIgnoreCase("nation")) {
            if ((args.length == 1) || TownyUniverse.getInstance().getNations().isEmpty()) return false;
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
            if (nationTopCache.get(args[1]) == null) {
                if(args[1].equals("total")) {
                	nationTopCache.put(args[1], List.of(TownyUniverse.getInstance().getNations()).stream().map(nation ->
                	TownyUniverse.getInstance().getNation(nation.toArray()[0].toString())
            			).sorted((v1, v2) ->  (MmoPlayer.getNationTotalLevel(v2)).compareTo(MmoPlayer.getNationTotalLevel(v1))).collect(Collectors.toList()));
                }
                else {
                	nationTopCache.put(args[1], List.of(TownyUniverse.getInstance().getNations()).stream().map(nation ->
                	TownyUniverse.getInstance().getNation(nation.toArray()[0].toString())
            			).sorted((v1, v2) ->  (MmoPlayer.getNationTalentLevel(v2, args[1])).compareTo(MmoPlayer.getNationTalentLevel(v1, args[1]))).collect(Collectors.toList()));
                }
                scheduleCacheClear(args[1]);
            }
            List<Nation> orderedNations = nationTopCache.get(args[1]);

            if(args[1].equals("total")) {
            	sender.sendMessage("§3----------§8[Page §7" + (page+1) + "§8]§3----------");
            	for (int i = page*10; i < (orderedNations.size() < (page+1) *10 ? orderedNations.size() : (page+1) *10); i++) {
                	Nation nation = orderedNations.get(i);
                	sender.sendMessage("§e" + (i+1) + ". §a" + nation.getName() + " §6- §3total §9" + MmoPlayer.getNationTotalLevel(nation));
            	}
            }
            else {
            	sender.sendMessage("§3----------§8[Page §7" + (page+1) + "§8]§3----------");
            	for (int i = page*10; i < (orderedNations.size() < (page+1) *10 ? orderedNations.size() : (page+1) *10); i++) {
                	Nation nation = orderedNations.get(i);
                	sender.sendMessage("§e" + (i+1) + ". §a" + nation.getName() + " §6- §3level §9" + MmoPlayer.getNationTalentLevel(nation, args[1]));
            	}
            }
        }

        return true;
    }
    private void scheduleCacheClear(String talent) {
        new BukkitRunnable() {
            @Override
            public void run() {
                townTopCache.remove(talent);
                nationTopCache.remove(talent);
            }
        }.runTaskLater(LaBoulangerieMmo.PLUGIN, 20*60);
    }
}
