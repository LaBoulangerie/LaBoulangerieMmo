package net.laboulangerie.laboulangeriemmo.api.player;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.abilities.mining.MarkedBlocksManager;
import net.laboulangerie.laboulangeriemmo.events.MmoPlayerBreakBlockEvent;
import net.laboulangerie.laboulangeriemmo.events.PlayerEarnsXpEvent;

public class MmoPlayerListener implements Listener {
    private MmoPlayerManager mmoPlayerManager;

    public MmoPlayerListener() {
        mmoPlayerManager = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (mmoPlayerManager.getPlayer(player) != null)
            return;
        mmoPlayerManager.loadPlayerData(player);
        MarkedBlocksManager.manager().setupTeams(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        mmoPlayerManager.savePlayerData(player);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        MmoPlayer mmoPlayer = mmoPlayerManager.getPlayer(player);
        MmoPlayerBreakBlockEvent breakBlockEvent = new MmoPlayerBreakBlockEvent(player, mmoPlayer, block);
        LaBoulangerieMmo.PLUGIN.getServer().getPluginManager().callEvent(breakBlockEvent);
    }

    @EventHandler
    public void onPlayerEarnsXp(PlayerEarnsXpEvent event) {
    }
}
