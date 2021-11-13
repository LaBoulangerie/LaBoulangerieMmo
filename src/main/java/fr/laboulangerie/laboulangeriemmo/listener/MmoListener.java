package fr.laboulangerie.laboulangeriemmo.listener;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class MmoListener implements Listener {
    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        Talent talent = event.getTalent();
        LaBoulangerieMmo.ECONOMY.depositPlayer((OfflinePlayer) player, talent.getLevelXp(0.2));
        player.sendMessage(
            "§aVous êtes passé au niveau §e"+talent.getLevel(0.2)
            +"§a en §e"+talent.getDisplayName()
            +"§a, vous gagnez §e"+talent.getLevelXp(0.2)+"$");
    }
}
