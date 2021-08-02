package fr.laboulangerie.laboulangeriemmo.blockus;

import com.github.luben.zstd.Zstd;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class BlockusInputStream extends FileInputStream {

    public BlockusInputStream(@NotNull File file) throws FileNotFoundException {
        super(file);
    }

    public BlockusDataHolder readBlockuses() throws IOException, ClassNotFoundException {
        int size = this.read();
        if (size == -1) return null;
        byte[] buffer = new byte[size];

        this.read(buffer, 4, size);
        byte[] data = Zstd.decompress(buffer, size);
        ObjectInput oi = new ObjectInputStream(new ByteArrayInputStream(data));

        return (BlockusDataHolder) oi.readObject();
    }
}
