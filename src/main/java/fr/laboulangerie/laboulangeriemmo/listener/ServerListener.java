package fr.laboulangerie.laboulangeriemmo.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.core.MarkedBlocksManager;

public class ServerListener implements Listener {
    public ServerListener() {}

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        boolean hasMetaData = block.hasMetadata("laboulangerie:placed");
        if (!hasMetaData) {
            block.setMetadata("laboulangerie:placed",
                    new FixedMetadataValue(LaBoulangerieMmo.PLUGIN, player.getUniqueId().toString())
            );
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        MarkedBlocksManager.manager().unmarkBlock(event.getClickedBlock());
    }
}
