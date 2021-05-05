package fr.laboulangerie.laboulangeriemmo.blockus;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockusRestoration extends BukkitRunnable {

    private LaBoulangerieMmo laBoulangerieMmo;

    public BlockusRestoration(BlockusDataManager blockusDataManager) {
        this.blockusDataManager = blockusDataManager;
    }

    @Override
    public void run() {

    }
}
