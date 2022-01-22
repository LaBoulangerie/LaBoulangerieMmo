package net.laboulangerie.laboulangeriemmo.blockus;

import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class BlockusRestoration extends BukkitRunnable {

    public BlockusRestoration() {
    }

    @Override
    public void run() {
        BlockusDataHolder blockusDataHolder = LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder();
        blockusDataHolder.getBlockuses().forEach(blockus -> blockus.markAsBlockus());
    }
}
