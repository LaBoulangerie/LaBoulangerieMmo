package net.laboulangerie.laboulangeriemmo.blockus;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class BlockusRestoration extends BukkitRunnable {

    public BlockusRestoration() {
    }

    @Override
    public void run() {
        AtomicReference<Boolean> warnedWorldRemoval = new AtomicReference<>(false);
        BlockusDataHolder blockusDataHolder = LaBoulangerieMmo.PLUGIN.getBlockusDataManager().getBlockusDataHolder();

        for (Iterator<Blockus> iterator = blockusDataHolder.getBlockuses().iterator(); iterator.hasNext();) {
            Blockus blockus = iterator.next();
            try {
                blockus.markAsBlockus();
            } catch (NullPointerException e) {
                iterator.remove();

                if (warnedWorldRemoval.get()) return; //Warn only on the first failure
                warnedWorldRemoval.set(true);
                LaBoulangerieMmo.PLUGIN.getLogger().warning("A world has been deleted so all its blockuses have been dropped.");
            }
        }
    }
}
