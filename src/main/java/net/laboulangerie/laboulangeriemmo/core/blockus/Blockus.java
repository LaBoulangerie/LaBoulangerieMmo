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

    private UUID worldId;
    private int x, y, z;

    private Map<String, Object> metaData;

    public Blockus(Block block) {
        this.worldId = block.getWorld().getUID();
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();

        this.metaData = new HashMap<>();
    }

    public Blockus(Location location) {
        this.worldId = location.getWorld().getUID();
        this.x = (int) location.getX();
        this.y = (int) location.getY();
        this.z = (int) location.getZ();

        this.metaData = new HashMap<>();
    }

    public void putMetadata(String key, Object value) {
        this.metaData.put(key, value);
    }

    public void markAsBlockus() {
        Block block = Bukkit.getWorld(this.worldId).getBlockAt(x, y, z);
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
}
