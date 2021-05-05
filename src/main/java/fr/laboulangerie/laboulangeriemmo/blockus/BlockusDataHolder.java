package fr.laboulangerie.laboulangeriemmo.blockus;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BlockusDataHolder implements Serializable {

    private Set<Blockus> blockuses;

    public BlockusDataHolder() {
        this.blockuses = new HashSet<>();
    }

    public void addBlockus(Blockus blockus) {
        this.blockuses.add(blockus);
    }

    public Set<Blockus> getBlockuses() {
        return this.blockuses;
    }
}
