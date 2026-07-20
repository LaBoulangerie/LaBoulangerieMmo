package net.laboulangerie.laboulangeriemmo.core.blockus.redis;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.blockus.Blockus;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisException;

public class RedisBlockusHolder {
    private static final String REDIS_KEY = "blockus";

    private final Map<String, Blockus> blockuses;
    private final Set<String> deletionTombstones;
    private final RedisClient redis;
    private final Logger logger;
    private final LongSupplier currentTimeMillis;
    private final long retryDelayMillis;

    private boolean redisUnavailable;
    private long retryAfterMillis;

    public RedisBlockusHolder() {
        this(createRedisClient(LaBoulangerieMmo.PLUGIN.getConfig()),
                LaBoulangerieMmo.PLUGIN.getLogger(), System::currentTimeMillis,
                TimeUnit.SECONDS.toMillis(Math.max(1L,
                        LaBoulangerieMmo.PLUGIN.getConfig().getLong("redis.retry-delay-seconds", 30L))));
    }

    RedisBlockusHolder(RedisClient redis, Logger logger, LongSupplier currentTimeMillis,
            long retryDelayMillis) {
        this.blockuses = new ConcurrentHashMap<>();
        this.deletionTombstones = ConcurrentHashMap.newKeySet();
        this.redis = redis;
        this.logger = logger;
        this.currentTimeMillis = currentTimeMillis;
        this.retryDelayMillis = Math.max(1L, retryDelayMillis);
    }

    public void addBlockus(Blockus blockus) {
        String id = blockus.getId();
        blockuses.put(id, blockus);
        deletionTombstones.remove(id);
        writeRedis("add blockus", () -> redis.hset(REDIS_KEY, id, "d"));
    }

    public void removeBlockus(Blockus blockus) {
        String id = blockus.getId();
        blockuses.remove(id);
        if (writeRedis("remove blockus", () -> redis.hdel(REDIS_KEY, id))) {
            deletionTombstones.remove(id);
        } else {
            deletionTombstones.add(id);
        }
    }

    public Blockus getBlockus(Block block) {
        String id = getId(block.getLocation());
        Blockus blockus = blockuses.get(id);
        if (blockus != null || deletionTombstones.contains(id)) return blockus;

        if (readRedis("lookup blockus", () -> redis.hexists(REDIS_KEY, id), false)) {
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
        return readRedis("count blockuses", () -> redis.hlen(REDIS_KEY),
                (long) blockuses.size());
    }

    private boolean writeRedis(String operation, Runnable action) {
        if (!canAttemptRedis()) return false;
        try {
            action.run();
            markRedisAvailable();
            return true;
        } catch (JedisException exception) {
            markRedisUnavailable(operation, exception);
            return false;
        }
    }

    private <T> T readRedis(String operation, Supplier<T> action, T fallback) {
        if (!canAttemptRedis()) return fallback;
        try {
            T result = action.get();
            markRedisAvailable();
            return result;
        } catch (JedisException exception) {
            markRedisUnavailable(operation, exception);
            return fallback;
        }
    }

    private synchronized boolean canAttemptRedis() {
        return !redisUnavailable || currentTimeMillis.getAsLong() >= retryAfterMillis;
    }

    private synchronized void markRedisUnavailable(String operation, JedisException exception) {
        redisUnavailable = true;
        retryAfterMillis = currentTimeMillis.getAsLong() + retryDelayMillis;
        logger.log(Level.WARNING,
                "Redis unavailable while trying to " + operation
                        + "; using the local blockus cache for " + retryDelayMillis + " ms",
                exception);
    }

    private synchronized void markRedisAvailable() {
        if (!redisUnavailable) return;
        redisUnavailable = false;
        logger.info("Redis connection restored; blockus operations are using Redis again");
    }

    private static RedisClient createRedisClient(FileConfiguration config) {
        JedisPooled jedis = new JedisPooled("localhost", config.getInt("redis.port"));
        return new RedisClient() {
            @Override
            public void hset(String key, String field, String value) {
                jedis.hset(key, field, value);
            }

            @Override
            public void hdel(String key, String field) {
                jedis.hdel(key, field);
            }

            @Override
            public boolean hexists(String key, String field) {
                return jedis.hexists(key, field);
            }

            @Override
            public long hlen(String key) {
                return jedis.hlen(key);
            }
        };
    }

    interface RedisClient {
        void hset(String key, String field, String value);

        void hdel(String key, String field);

        boolean hexists(String key, String field);

        long hlen(String key);
    }
}
