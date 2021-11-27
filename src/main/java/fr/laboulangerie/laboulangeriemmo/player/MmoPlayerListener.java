package fr.laboulangerie.laboulangeriemmo.player;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.core.MarkedBlocksManager;
import fr.laboulangerie.laboulangeriemmo.events.MmoPlayerBreakBlockEvent;
import fr.laboulangerie.laboulangeriemmo.events.PlayerEarnsXpEvent;

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
        MarkedBlocksManager.manager().setupTeams(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.mmoPlayerManager.savePlayerData(player);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        MmoPlayer mmoPlayer = this.mmoPlayerManager.getPlayer(player);
        MmoPlayerBreakBlockEvent breakBlockEvent = new MmoPlayerBreakBlockEvent(player, mmoPlayer, block);
        this.laBoulangerieMmo.getServer().getPluginManager().callEvent(breakBlockEvent);
    }

    @EventHandler
    public void onPlayerEarnsXp(PlayerEarnsXpEvent event) {
    }
}
