package net.laboulangerie.laboulangeriemmo.blockus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;

import com.github.luben.zstd.Zstd;

import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class BlockusOutputStream extends FileOutputStream {

    public BlockusOutputStream(@NotNull File file) throws FileNotFoundException {
        super(file);
    }

    public void writeBlockuses(BlockusDataHolder blockusDataHolder) throws IOException {
        try { // Crash the saving process before overwriting the save file
            Zstd.compress(new byte[1]);
        } catch (NoClassDefFoundError e) {
            LaBoulangerieMmo.PLUGIN.getLogger().log(Level.SEVERE,
                    "Plugin's JAR has been replaced, can't save blockuses, next time do /mmo reload before replacing the JAR");
            return;
        }
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
