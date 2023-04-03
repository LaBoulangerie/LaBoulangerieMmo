package net.laboulangerie.laboulangeriemmo.commands;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayerManager;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype;
import net.laboulangerie.laboulangeriemmo.api.xpboost.XpBoostObj;
import net.laboulangerie.laboulangeriemmo.core.XpBoostManager;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XpBoost implements CommandExecutor, TabCompleter {

    private FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 5){
            if(args[0].equalsIgnoreCase("add")){
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                if(!(NumberUtils.isParsable(args[2]))) {
                    sender.sendMessage("§4Boost argument is not numeric");
                    return true;
                }
                if(!(NumberUtils.isParsable(args[4]))) {
                    sender.sendMessage("§4Time argument is not numeric");
                    return true;
                }
                double boost = Double.parseDouble(args[2]);
                String identifier = args[3];
                int time = Integer.parseInt(args[4]);
                MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(offlinePlayer);
                TalentArchetype talentTarget = LaBoulangerieMmo.talentsRegistry.getTalent(identifier);
                LaBoulangerieMmo.PLUGIN.getXpBoostManager().add(new XpBoostObj(mmoPlayer, talentTarget, boost, time));
                for(Player p : Bukkit.getOnlinePlayers())
                    p.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"))
                                    .append(MiniMessage.miniMessage().deserialize( "<#F9C784>"+offlinePlayer.getName()+" vient d'activer un boost x"+boost+" pour le métier "+talentTarget.displayName+" !")));
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1)
            return Arrays.asList("add");
        if(args[0].equalsIgnoreCase("add") && args.length > 1){
            if(args.length == 2){
                List<String> playerNameList = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach((player -> {
                    playerNameList.add(player.getName());
                }));
                return playerNameList;
            }
            if(args.length == 3)
                return Arrays.asList("1.5", "2", "2.5", "3");
            if(args.length == 4){
                List<String> talentIdentifier = new ArrayList<>();
                LaBoulangerieMmo.talentsRegistry.getTalents().forEach((key, value) -> {
                    talentIdentifier.add(value.identifier);
                });
                return talentIdentifier;
            }
            if(args.length == 5)
                return Arrays.asList("60", "120", "180", "240");
        }


        return null;
    }
}