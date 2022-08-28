package net.laboulangerie.laboulangeriemmo.core.blockus;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.destroystokyo.paper.event.block.TNTPrimeEvent;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class BlockusListener implements Listener {

    public BlockusListener() {
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Blockus blockus = new Blockus(block);
        blockus.putMetadata("laboulangerie:placed", player.getUniqueId());
        blockus.markAsBlockus();
        LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder().addBlockus(blockus);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        BlockusDataHolder dataHolder = LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder();
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
        BlockusDataHolder dataHolder = LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder();
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
        unMark(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        unMark(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onExplode(BlockExplodeEvent event) {
        event.blockList().stream().forEach(block -> unMark(block));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().stream().forEach(block -> unMark(block));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrimed(TNTPrimeEvent event) {
        unMark(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBurn(BlockBurnEvent event) {
        unMark(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTreeGrow(StructureGrowEvent event) {
    	for(BlockState blockState : event.getBlocks()) {
    		Block block = blockState.getBlock();
            unMark(block);
    	}
    }

    /**
     * Removes the block from blockuses if it is a blockus
     *
     * @param block
     */
    private void unMark(Block block) {
        if (!block.hasMetadata("laboulangerie:placed"))
            return;
        block.removeMetadata("laboulangerie:placed", LaBoulangerieMmo.PLUGIN);
        BlockusDataHolder dataHolder = LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder();
        dataHolder.removeBlockus(dataHolder.getBlockus(block));
    }
}
