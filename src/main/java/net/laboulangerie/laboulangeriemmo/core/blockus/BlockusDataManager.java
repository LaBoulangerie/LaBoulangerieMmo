package net.laboulangerie.laboulangeriemmo.core.blockus;

import java.io.*;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class BlockusDataManager {

    private File blockusFile;

    private BlockusDataHolder blockusDataHolder;

    public BlockusDataManager(String path) {
        blockusFile = new File(path);
        File parentFile = blockusFile.getParentFile();
        if (!parentFile.exists())
            parentFile.mkdir();

        try {
            if (!blockusFile.exists())
                blockusFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            blockusDataHolder = readBlockuses();
        } catch (IOException | ClassNotFoundException e) {
            blockusDataHolder = new BlockusDataHolder();
            LaBoulangerieMmo.PLUGIN.getLogger().warning("Something went wrong while trying to restore blockuses:");
            e.printStackTrace();
        }
        if (blockusDataHolder == null) {
            blockusDataHolder = new BlockusDataHolder();
        }

    }

    public void writeBlockuses() throws IOException {
        BlockusOutputStream bos = new BlockusOutputStream(blockusFile);
        bos.writeBlockuses(blockusDataHolder);
        bos.close();
    }

    public BlockusDataHolder readBlockuses() throws IOException, ClassNotFoundException {
        BlockusInputStream bis = new BlockusInputStream(blockusFile);
        BlockusDataHolder data = bis.readBlockuses();
        bis.close();
        return data;
    }

    public BlockusDataHolder getBlockusDataHolder() {
        return blockusDataHolder;
    }
}
