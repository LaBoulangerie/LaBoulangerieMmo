package net.laboulangerie.laboulangeriemmo.commands.talenttree;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;

public class TalentTree implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args) {
        Set<String> talentIdentifiers = LaBoulangerieMmo.talentsRegistry.getTalents().keySet();

        if (args.length != 1 || !talentIdentifiers.contains(args[0])) {
            sender.sendMessage("§4Veuillez spécifier un nom de talent.");
            return false;
        }
        String queriedTalent = args[0];
        Player player = (Player) sender;

        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);
        Talent talent = mmoPlayer.getTalent(queriedTalent);

        Inventory talentTreeInv = new TalentTreeInv(player, talent).getInventory();
        player.openInventory(talentTreeInv);

        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1) return null;

        List<String> talentIdentifiers =
                LaBoulangerieMmo.talentsRegistry.getTalents().keySet().stream().collect(Collectors.toList());

        return talentIdentifiers;
    }


}
