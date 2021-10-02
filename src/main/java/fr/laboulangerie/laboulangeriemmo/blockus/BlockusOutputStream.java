package fr.laboulangerie.laboulangeriemmo.blockus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import com.github.luben.zstd.Zstd;

import org.jetbrains.annotations.NotNull;

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
