package net.laboulangerie.laboulangeriemmo.core.blockus.redis;

import java.io.IOException;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusDataHolder;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusManager;
import redis.clients.jedis.JedisPooled;

public class RedisBlockusManager implements BlockusManager {
    JedisPooled jedis;

    public RedisBlockusManager() {
        jedis = new JedisPooled("localhost", 6379);

    }

    @Override
    public void writeBlockuses() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeBlockuses'");
    }

    @Override
    public BlockusDataHolder readBlockuses() throws IOException, ClassNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readBlockuses'");
    }

    @Override
    public BlockusDataHolder getBlockusDataHolder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlockusDataHolder'");
    }
    
}
