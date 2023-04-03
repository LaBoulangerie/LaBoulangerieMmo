package net.laboulangerie.laboulangeriemmo.commands;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype;
import net.laboulangerie.laboulangeriemmo.api.xpboost.XpBoostObj;
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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();
        
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
                Double boost = Double.parseDouble(args[2]);
                String identifier = args[3];
                int time = Integer.parseInt(args[4]);
                MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(offlinePlayer);
                TalentArchetype talentTarget = LaBoulangerieMmo.talentsRegistry.getTalent(identifier);
                LaBoulangerieMmo.PLUGIN.getXpBoostManager().add(new XpBoostObj(mmoPlayer, talentTarget, boost, time));

                List<TagResolver.Single> placeholders = Arrays.asList(
                    Placeholder.parsed("boost", boost.toString()),
                    Placeholder.parsed("talent", talentTarget.displayName),
                    Placeholder.parsed("author", offlinePlayer.getName())
                );

                Component prefix = MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"));
                Component notification =  MiniMessage.miniMessage().deserialize(config.getString("lang.xp_boost.notif"),
                    TagResolver.resolver(placeholders)); 

                for(Player p : Bukkit.getOnlinePlayers())
                    p.sendMessage(prefix.append(notification));
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