package fr.laboulangerie.laboulangeriemmo.blockus;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockusListener implements Listener {

    private LaBoulangerieMmo laBoulangerieMmo;

    public BlockusListener(LaBoulangerieMmo laBoulangerieMmo) {
        this.laBoulangerieMmo = laBoulangerieMmo;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Blockus blockus = new Blockus(block);
        blockus.putMetadata("laboulangerie:blockus", player.getUniqueId());
        blockus.markAsBlockus(this.laBoulangerieMmo);
        this.laBoulangerieMmo.getBlockusDataManager().getBlockusDataHolder().addBlockus(blockus);
    }


}
