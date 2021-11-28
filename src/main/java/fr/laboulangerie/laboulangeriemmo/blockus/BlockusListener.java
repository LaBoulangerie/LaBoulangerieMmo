package fr.laboulangerie.laboulangeriemmo.blockus;

import java.util.List;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.destroystokyo.paper.event.block.TNTPrimeEvent;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

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
        blockus.putMetadata("laboulangerie:placed", player.getUniqueId());
        blockus.markAsBlockus();
        this.laBoulangerieMmo.getBlockusDataManager().getBlockusDataHolder().addBlockus(blockus);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        BlockusDataHolder dataHolder = this.laBoulangerieMmo.getBlockusDataManager().getBlockusDataHolder();
        Vector vec = event.getDirection().getDirection();
        event.getBlocks().stream().filter(block -> block.hasMetadata("laboulangerie:placed")).forEach(block -> {
            List<MetadataValue> value = block.getMetadata("laboulangerie:placed");

            block.removeMetadata("laboulangerie:placed", LaBoulangerieMmo.PLUGIN);
            dataHolder.removeBlockus(dataHolder.getBlockus(block));
            
            Blockus blockus = new Blockus(block.getLocation().add(vec));
            blockus.putMetadata("laboulangerie:placed", value);
            blockus.markAsBlockus();
        });
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        BlockusDataHolder dataHolder = this.laBoulangerieMmo.getBlockusDataManager().getBlockusDataHolder();
        Vector vec = event.getDirection().getDirection();
        event.getBlocks().stream().filter(block -> block.hasMetadata("laboulangerie:placed")).forEach(block -> {
            List<MetadataValue> value = block.getMetadata("laboulangerie:placed");

            block.removeMetadata("laboulangerie:placed", LaBoulangerieMmo.PLUGIN);
            dataHolder.removeBlockus(dataHolder.getBlockus(block));

            Blockus blockus = new Blockus(block.getLocation().add(vec));
            blockus.putMetadata("laboulangerie:placed", value);
            blockus.markAsBlockus();
        });
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDestroyed(BlockDestroyEvent event) {
        if (!event.getBlock().hasMetadata("laboulangerie:placed")) return;
        event.getBlock().removeMetadata("laboulangerie:placed", LaBoulangerieMmo.PLUGIN);
        BlockusDataHolder dataHolder = this.laBoulangerieMmo.getBlockusDataManager().getBlockusDataHolder();
        dataHolder.removeBlockus(dataHolder.getBlockus(event.getBlock()));
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (!event.getBlock().hasMetadata("laboulangerie:placed")) return;
        event.getBlock().removeMetadata("laboulangerie:placed", LaBoulangerieMmo.PLUGIN);
        BlockusDataHolder dataHolder = this.laBoulangerieMmo.getBlockusDataManager().getBlockusDataHolder();
        dataHolder.removeBlockus(dataHolder.getBlockus(event.getBlock()));
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onExplode(BlockExplodeEvent event) {
        event.blockList().stream().filter(block -> block.hasMetadata("laboulangerie:placed")).forEach(block -> {
            block.removeMetadata("laboulangerie:placed", LaBoulangerieMmo.PLUGIN);
            BlockusDataHolder dataHolder = this.laBoulangerieMmo.getBlockusDataManager().getBlockusDataHolder();
            dataHolder.removeBlockus(dataHolder.getBlockus(block));
        });
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().stream().filter(block -> block.hasMetadata("laboulangerie:placed")).forEach(block -> {
            block.removeMetadata("laboulangerie:placed", LaBoulangerieMmo.PLUGIN);
            BlockusDataHolder dataHolder = this.laBoulangerieMmo.getBlockusDataManager().getBlockusDataHolder();
            dataHolder.removeBlockus(dataHolder.getBlockus(block));
        });
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrimed(TNTPrimeEvent event) {
        if (!event.getBlock().hasMetadata("laboulangerie:placed")) return;
        event.getBlock().removeMetadata("laboulangerie:placed", LaBoulangerieMmo.PLUGIN);
        BlockusDataHolder dataHolder = this.laBoulangerieMmo.getBlockusDataManager().getBlockusDataHolder();
        dataHolder.removeBlockus(dataHolder.getBlockus(event.getBlock()));
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBurn(BlockBurnEvent event) {
        if (!event.getBlock().hasMetadata("laboulangerie:placed")) return;
        event.getBlock().removeMetadata("laboulangerie:placed", LaBoulangerieMmo.PLUGIN);
        BlockusDataHolder dataHolder = this.laBoulangerieMmo.getBlockusDataManager().getBlockusDataHolder();
        dataHolder.removeBlockus(dataHolder.getBlockus(event.getBlock()));
    }
}
