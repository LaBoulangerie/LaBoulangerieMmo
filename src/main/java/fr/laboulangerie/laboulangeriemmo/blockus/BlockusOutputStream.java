package fr.laboulangerie.laboulangeriemmo.blockus;

import com.github.luben.zstd.Zstd;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.ByteBuffer;

public class BlockusOutputStream extends FileOutputStream {


    public BlockusOutputStream(@NotNull File file) throws FileNotFoundException {
        super(file);
    }

    public void writeBlockuses(BlockusDataHolder blockusDataHolder) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(blockusDataHolder);

        ByteBuffer buffer = ByteBuffer.allocate(4);
        byte[] compressed = Zstd.compress(byteArrayOutputStream.toByteArray());
        buffer.putInt(byteArrayOutputStream.size());

        this.write(buffer.array());
        this.write(compressed);
        this.flush();
    }

}
