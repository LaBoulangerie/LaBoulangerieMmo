package net.laboulangerie.laboulangeriemmo.core.blockus;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.block.Block;

public class BlockusDataHolder implements Serializable {

    private Set<Blockus> blockuses;

    public BlockusDataHolder() {
        this.blockuses = new HashSet<>();
    }

    public void addBlockus(Blockus blockus) {
        this.blockuses.add(blockus);
    }

    public void removeBlockus(Blockus blockus) {
        blockuses.remove(blockus);
    }

    public Blockus getBlockus(Block block) {
        Iterator<Blockus> it = blockuses.iterator();
        while (it.hasNext()) {
            Blockus blockus = it.next();
            if (blockus.getX() == block.getLocation().getX()
                    && blockus.getY() == block.getLocation().getY()
                    && blockus.getZ() == block.getLocation().getZ()
                    && blockus.getWorldId() == block.getLocation().getWorld().getUID())
                return blockus;
        }
        return null;
    }

    public Set<Blockus> getBlockuses() {
        return this.blockuses;
    }
}
