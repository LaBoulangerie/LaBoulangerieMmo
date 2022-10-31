package net.laboulangerie.laboulangeriemmo.core.blockus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class Blockus implements Serializable {
    private static final long serialVersionUID = 10L;

    private UUID worldId;
    private int x, y, z;

    private Map<String, Object> metaData;

    public Blockus(Block block) {
        worldId = block.getWorld().getUID();
        x = block.getX();
        y = block.getY();
        z = block.getZ();

        metaData = new HashMap<>();
    }

    public Blockus(Location location) {
        worldId = location.getWorld().getUID();
        x = (int) location.getX();
        y = (int) location.getY();
        z = (int) location.getZ();

        metaData = new HashMap<>();
    }

    public void putMetadata(String key, Object value) {
        metaData.put(key, value);
    }

    public void markAsBlockus() {
        Block block = Bukkit.getWorld(worldId).getBlockAt(x, y, z);
        metaData.entrySet().stream()
                .filter(entry -> !block.hasMetadata(entry.getKey()))
                .forEach(entry -> block.setMetadata(entry.getKey(),
                        new FixedMetadataValue(LaBoulangerieMmo.PLUGIN, entry.getValue())));

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public UUID getWorldId() {
        return worldId;
    }

    public String getId() {
        return x + ";" + y + ";" + z + ";" + worldId.toString();
    }
}
