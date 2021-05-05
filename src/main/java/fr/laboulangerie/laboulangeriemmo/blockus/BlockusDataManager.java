package fr.laboulangerie.laboulangeriemmo.blockus;

import java.io.*;

public class BlockusDataManager {

    private File blockusFile;

    private BlockusDataHolder blockusDataHolder;

    public BlockusDataManager(String path) {
        this.blockusFile = new File(path);
        File parentFile = this.blockusFile.getParentFile();
        if (!parentFile.exists())parentFile.mkdir();

        try {
            if (!this.blockusFile.exists())this.blockusFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.blockusDataHolder = this.readBlockuses();
        } catch (IOException | ClassNotFoundException ignored) {
            this.blockusDataHolder = new BlockusDataHolder();
        }
    }


    public void writeBlockuses() throws IOException {
        BlockusOutputStream bos = new BlockusOutputStream(this.blockusFile);
        bos.writeBlockuses(this.blockusDataHolder);
    }

    public BlockusDataHolder readBlockuses() throws IOException, ClassNotFoundException {
        BlockusInputStream bis = new BlockusInputStream(this.blockusFile);
        return bis.readBlockuses();
    }

    public BlockusDataHolder getBlockusDataHolder() {
        return blockusDataHolder;
    }
}
