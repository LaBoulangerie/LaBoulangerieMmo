package net.laboulangerie.laboulangeriemmo.core.blockus;

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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (LaBoulangerieMmo.PLUGIN.getConfig().getStringList("blockus-ignored-blocks").contains(block.getType().toString())) return;

        Blockus blockus = new Blockus(block);
        blockus.putMetadata("laboulangerie:placed", player.getUniqueId().toString());
        blockus.markAsBlockus();
        LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder().addBlockus(blockus);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (event.isCancelled()) return;
        BlockusDataHolder dataHolder = LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder();
        Vector vec = event.getDirection().getDirection();
        event.getBlocks().stream().filter(block -> block.hasMetadata("laboulangerie:placed")).forEach(block -> {
            MetadataValue value = block.getMetadata("laboulangerie:placed").get(0);

            dataHolder.removeBlockus(dataHolder.getBlockus(block));

            Blockus blockus = new Blockus(block.getLocation().add(vec));
            blockus.putMetadata("laboulangerie:placed", value.asString());
            dataHolder.addBlockus(blockus);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (event.isCancelled()) return;
        BlockusDataHolder dataHolder = LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder();
        Vector vec = event.getDirection().getDirection();
        event.getBlocks().stream().filter(block -> block.hasMetadata("laboulangerie:placed")).forEach(block -> {
            MetadataValue value = block.getMetadata("laboulangerie:placed").get(0);

            block.removeMetadata("laboulangerie:placed", LaBoulangerieMmo.PLUGIN);
            dataHolder.removeBlockus(dataHolder.getBlockus(block));

            Blockus blockus = new Blockus(block.getLocation().add(vec));
            blockus.putMetadata("laboulangerie:placed", value.asString());
            blockus.markAsBlockus();
            dataHolder.addBlockus(blockus);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDestroyed(BlockDestroyEvent event) {
        if (event.isCancelled()) return;
        unMark(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        unMark(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onExplode(BlockExplodeEvent event) {
        if (event.isCancelled()) return;
        event.blockList().stream().forEach(block -> unMark(block));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        event.blockList().stream().forEach(block -> unMark(block));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrimed(TNTPrimeEvent event) {
        if (event.isCancelled()) return;
        unMark(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBurn(BlockBurnEvent event) {
        if (event.isCancelled()) return;
        unMark(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTreeGrow(StructureGrowEvent event) {
        if (event.isCancelled()) return;
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
