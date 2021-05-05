package fr.laboulangerie.laboulangeriemmo.player;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MmoPlayerListener implements Listener {


    private LaBoulangerieMmo laBoulangerieMmo;
    private MmoPlayerManager mmoPlayerManager;

    public MmoPlayerListener(LaBoulangerieMmo laBoulangerieMmo) {
        this.laBoulangerieMmo = laBoulangerieMmo;
        this.mmoPlayerManager = laBoulangerieMmo.getMmoPlayerManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.mmoPlayerManager.loadPlayerData(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.mmoPlayerManager.savePlayerData(player);
    }
}
