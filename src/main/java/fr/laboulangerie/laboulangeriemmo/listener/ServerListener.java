package fr.laboulangerie.laboulangeriemmo.listener;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ServerListener implements Listener {

    private LaBoulangerieMmo laBoulangerieMmo;

    public ServerListener(LaBoulangerieMmo laBoulangerieMmo) {
        this.laBoulangerieMmo = laBoulangerieMmo;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        boolean hasMetaData = block.hasMetadata("laboulangerie:placed");
        if (!hasMetaData) {
            block.setMetadata("laboulangerie:placed",
                    new FixedMetadataValue(this.laBoulangerieMmo, player.getUniqueId().toString())
            );
        }
    }
}
