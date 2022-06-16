package net.laboulangerie.laboulangeriemmo.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;

public class Combo implements CommandExecutor, TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return Arrays.asList("on", "off", "toggle");
        return Arrays.asList("");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§4This command is reserved to players!");
            return true;
        }
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer((Player) sender);
        Boolean state;

        if (args.length == 0) { // we want to do the toggle behavior if no parameter is passed
            args = new String[1];
            args[0] = "toggle";
        }
        if (args.length == 1 && !Arrays.asList("on", "off", "toggle").contains(args[0])) {
            return false;
        }
        state = args[0].equals("on") ? true : args[0].equals("off") ? false : !mmoPlayer.hasEnabledCombo();
        mmoPlayer.setEnableCombo(state);

        sender.sendMessage(MiniMessage.miniMessage().deserialize(LaBoulangerieMmo.PLUGIN.getConfig().getString("lang.prefix"))
                .append(MiniMessage.miniMessage().deserialize(LaBoulangerieMmo.PLUGIN.getConfig().getString("lang.messages.combo_toggle"), Placeholder.parsed("state", state == true ? "activé" : "désactivé"))));
        return true;
    } 
}
