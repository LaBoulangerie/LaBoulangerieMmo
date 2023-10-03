package net.laboulangerie.laboulangeriemmo.core.blockus;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.blockus.redis.RedisBlockusHolder;

public class BlockusListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();

        if (LaBoulangerieMmo.PLUGIN.getConfig().getStringList("blockus-ignored-blocks")
                .contains(block.getType().toString()))
            return;

        Blockus blockus = new Blockus(block);
        blockus.markAsBlockus();
        LaBoulangerieMmo.PLUGIN.getBlockusHolder().addBlockus(blockus);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (event.isCancelled()) return;
        RedisBlockusHolder dataHolder = LaBoulangerieMmo.PLUGIN.getBlockusHolder();
        Vector vec = event.getDirection().getDirection();

        event.getBlocks().stream().filter(block -> LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) != null).forEach(block -> {
            dataHolder.removeBlockus(dataHolder.getBlockus(block));

            Blockus blockus = new Blockus(block.getLocation().add(vec));
            dataHolder.addBlockus(blockus);

            new BukkitRunnable() {
                @Override
                public void run() {
                    blockus.markAsBlockus();
                }
            }.runTaskLater(LaBoulangerieMmo.PLUGIN, 0);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (event.isCancelled()) return;
        RedisBlockusHolder dataHolder = LaBoulangerieMmo.PLUGIN.getBlockusHolder();
        Vector vec = event.getDirection().getDirection();

        event.getBlocks().stream().filter(block -> LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) != null).forEach(block -> {
            dataHolder.removeBlockus(dataHolder.getBlockus(block));

            Blockus blockus = new Blockus(block.getLocation().add(vec));
            dataHolder.addBlockus(blockus);
            blockus.markAsBlockus();

            new BukkitRunnable() {
                @Override
                public void run() {
                    blockus.markAsBlockus();
                }
            }.runTaskLater(LaBoulangerieMmo.PLUGIN, 0);
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
        for (BlockState blockState : event.getBlocks()) {
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
        if (LaBoulangerieMmo.PLUGIN.getBlockusHolder().getBlockus(block) == null) return;
        RedisBlockusHolder dataHolder = LaBoulangerieMmo.PLUGIN.getBlockusHolder();
        dataHolder.removeBlockus(dataHolder.getBlockus(block));
    }
}
