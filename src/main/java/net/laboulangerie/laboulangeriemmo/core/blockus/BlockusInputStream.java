package net.laboulangerie.laboulangeriemmo.core.blockus;

import com.github.luben.zstd.Zstd;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.ByteBuffer;

public class BlockusInputStream extends FileInputStream {

    public BlockusInputStream(@NotNull File file) throws FileNotFoundException {
        super(file);
    }

    public BlockusDataHolder readBlockuses() throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[4];
        read(bytes);

        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        int size = wrapped.getInt();

        if (size == 0)
            return null;
        byte[] buffer = new byte[available()];

        read(buffer);
        byte[] data = Zstd.decompress(buffer, size);
        ObjectInput oi = new ObjectInputStream(new ByteArrayInputStream(data));
        return (BlockusDataHolder) oi.readObject();
    }
}
