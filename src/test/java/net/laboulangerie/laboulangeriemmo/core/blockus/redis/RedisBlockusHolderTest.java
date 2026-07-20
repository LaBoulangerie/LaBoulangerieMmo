package net.laboulangerie.laboulangeriemmo.core.blockus.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.laboulangerie.laboulangeriemmo.core.blockus.Blockus;
import redis.clients.jedis.exceptions.JedisConnectionException;

class RedisBlockusHolderTest {
    private RedisBlockusHolder.RedisClient redis;
    private AtomicLong clock;
    private RecordingHandler logs;
    private RedisBlockusHolder holder;

    @BeforeEach
    void setUp() {
        redis = mock(RedisBlockusHolder.RedisClient.class);
        clock = new AtomicLong(1_000L);
        logs = new RecordingHandler();
        Logger logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        logger.addHandler(logs);
        holder = new RedisBlockusHolder(redis, logger, clock::get, 30_000L);
    }

    @Test
    void keepsAddsInLocalCacheWhenRedisIsUnavailable() {
        Block block = blockAt(1, 2, 3);
        Blockus blockus = new Blockus(block);
        org.mockito.Mockito.doThrow(new JedisConnectionException("offline"))
                .when(redis).hset("blockus", blockus.getId(), "d");

        holder.addBlockus(blockus);

        assertSame(blockus, holder.getBlockus(block));
        assertEquals(1L, holder.getTotalBlockuses());
        assertEquals(1, logs.warningCount);
        verify(redis, never()).hlen("blockus");
    }

    @Test
    void missingBlockFailsOpenAndRetriesOnlyAfterDelay() {
        Block block = blockAt(4, 5, 6);
        when(redis.hexists("blockus", idOf(block)))
                .thenThrow(new JedisConnectionException("offline"))
                .thenReturn(false);

        assertNull(holder.getBlockus(block));
        assertNull(holder.getBlockus(block));
        verify(redis, times(1)).hexists("blockus", idOf(block));
        assertEquals(1, logs.warningCount);

        clock.addAndGet(30_000L);
        assertNull(holder.getBlockus(block));

        verify(redis, times(2)).hexists("blockus", idOf(block));
        assertEquals(1, logs.infoCount);
    }

    @Test
    void failedRemovalLeavesTombstoneThatPreventsRemoteResurrection() {
        Block block = blockAt(7, 8, 9);
        Blockus blockus = new Blockus(block);
        holder.addBlockus(blockus);
        org.mockito.Mockito.doThrow(new JedisConnectionException("offline"))
                .when(redis).hdel("blockus", blockus.getId());

        holder.removeBlockus(blockus);
        clock.addAndGet(30_000L);

        assertNull(holder.getBlockus(block));
        verify(redis, never()).hexists("blockus", blockus.getId());
    }

    @Test
    void healthyRedisCountRemainsAuthoritative() {
        when(redis.hlen("blockus")).thenReturn(42L);

        assertEquals(42L, holder.getTotalBlockuses());
        assertEquals(0, logs.warningCount);
    }

    private static Block blockAt(int x, int y, int z) {
        World world = mock(World.class);
        when(world.getUID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Location location = new Location(world, x, y, z);
        Block block = mock(Block.class);
        when(block.getWorld()).thenReturn(world);
        when(block.getX()).thenReturn(x);
        when(block.getY()).thenReturn(y);
        when(block.getZ()).thenReturn(z);
        when(block.getLocation()).thenReturn(location);
        return block;
    }

    private static String idOf(Block block) {
        Location location = block.getLocation();
        return location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ()
                + ";" + location.getWorld().getUID();
    }

    private static final class RecordingHandler extends Handler {
        private int warningCount;
        private int infoCount;

        @Override
        public void publish(LogRecord record) {
            if (record.getLevel().intValue() >= Level.WARNING.intValue()) warningCount++;
            if (record.getLevel() == Level.INFO) infoCount++;
        }

        @Override
        public void flush() {}

        @Override
        public void close() {}
    }
}
