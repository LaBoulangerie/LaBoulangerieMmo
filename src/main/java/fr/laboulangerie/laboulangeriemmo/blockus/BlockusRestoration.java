package fr.laboulangerie.laboulangeriemmo.blockus;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockusRestoration extends BukkitRunnable {

    public BlockusRestoration() {
    }

    @Override
    public void run() {
        BlockusDataHolder blockusDataHolder = LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder();
        blockusDataHolder.getBlockuses().forEach(blockus -> blockus.markAsBlockus());
    }
}
