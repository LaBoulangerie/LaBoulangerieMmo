package net.laboulangerie.laboulangeriemmo.core.blockus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockusDataHolder implements Serializable {

    private Map<String, Blockus> blockuses;

    public BlockusDataHolder() {
        blockuses = new HashMap<>();
    }

    public void addBlockus(Blockus blockus) {
        blockuses.put(getId(blockus), blockus);
    }

    public void removeBlockus(Blockus blockus) {
        blockuses.remove(getId(blockus));
    }

    public Blockus getBlockus(Block block) {
        return blockuses.get(getId(block.getLocation()));
    }

    public Map<String, Blockus> getBlockuses() {
        return blockuses;
    }

    private String getId(Location loc) {
        return loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";" + loc.getWorld().getUID().toString();
    }
    private String getId(Blockus blockus) {
        return blockus.getX() + ";" + blockus.getY() + ";" + blockus.getZ() + ";" + blockus.getWorldId().toString();
    }
}
