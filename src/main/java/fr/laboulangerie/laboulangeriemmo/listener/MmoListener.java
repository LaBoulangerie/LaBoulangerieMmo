package fr.laboulangerie.laboulangeriemmo.listener;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
import fr.laboulangerie.laboulangeriemmo.events.XpCountDownFinishedEvent;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;
import net.kyori.adventure.text.Component;

public class MmoListener implements Listener {
    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        Talent talent = event.getTalent();
        LaBoulangerieMmo.ECONOMY.depositPlayer((OfflinePlayer) player, 1_000);
        player.sendMessage(
            "§aVous êtes passé au niveau §e"+talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER)
            +"§a en §e"+talent.getDisplayName()
            +"§a, vous gagnez §e1000$");
    }

    @EventHandler
    public void onCountDownFinished(XpCountDownFinishedEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        player.sendMessage(Component.text(
            "§aVous avez gagné §e" + event.getAmount() + "§axp en §e"
            + event.getTalent().getDisplayName()));
    }
}
