package fr.laboulangerie.laboulangeriemmo.blockus;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockusRestoration extends BukkitRunnable {

    private LaBoulangerieMmo laBoulangerieMmo;

    public BlockusRestoration(LaBoulangerieMmo laBoulangerieMmo) {
        this.laBoulangerieMmo = laBoulangerieMmo;
    }

    @Override
    public void run() {
        BlockusDataHolder blockusDataHolder = this.laBoulangerieMmo.getBlockusDataManager().getBlockusDataHolder();
        blockusDataHolder.getBlockuses().forEach(blockus -> blockus.markAsBlockus(this.laBoulangerieMmo));
    }
}
