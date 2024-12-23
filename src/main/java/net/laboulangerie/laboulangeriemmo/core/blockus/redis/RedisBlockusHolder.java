package net.laboulangerie.laboulangeriemmo.core.blockus.redis;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.blockus.Blockus;
import redis.clients.jedis.JedisPooled;

public class RedisBlockusHolder {
    private Map<String, Blockus> blockuses;
    private JedisPooled jedis;

    public RedisBlockusHolder() {
        blockuses = new HashMap<>();
        FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();
        jedis = new JedisPooled("localhost", config.getInt("redis.port"));
    }

    public void addBlockus(Blockus blockus) {
        blockuses.put(blockus.getId(), blockus);
        jedis.hset("blockus", blockus.getId(), "d");
    }

    public void removeBlockus(Blockus blockus) {
        blockuses.remove(blockus.getId());
        jedis.hdel("blockus", blockus.getId());
    }

    public Blockus getBlockus(Block block) {
        String id = getId(block.getLocation());
        Blockus blockus = blockuses.get(id);
        if (blockus == null
            && jedis.hexists("blockus", getId(block.getLocation()))) {
                blockus = new Blockus(block);
                blockuses.put(blockus.getId(), blockus);
        }
        return blockus;
    }

    public Map<String, Blockus> getBlockuses() {
        return blockuses;
    }

    private String getId(Location loc) {
        return loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";"
                + loc.getWorld().getUID().toString();
    }

    public long getTotalBlockuses() {
        return jedis.hlen("blockus");
    }
}
