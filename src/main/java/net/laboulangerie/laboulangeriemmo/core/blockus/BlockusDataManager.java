package net.laboulangerie.laboulangeriemmo.core.blockus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.bukkit.Bukkit;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class BlockusDataManager {

    private File blockusFile;

    private BlockusDataHolder blockusDataHolder;

    public BlockusDataManager(String path) {
        blockusFile = new File(path);
        File parentFolder = blockusFile.getParentFile();
        if (!parentFolder.exists()) parentFolder.mkdir();

        if (!blockusFile.exists()) {
            try {
                blockusFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                blockusDataHolder = new BlockusDataHolder();
            }
            return;
        }

        try {
            blockusDataHolder = readBlockuses();
        } catch (IOException | ClassNotFoundException e) {
            LaBoulangerieMmo.PLUGIN.getLogger()
                    .warning("Something went wrong while trying to restore blockuses:");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(LaBoulangerieMmo.PLUGIN);
        }
    }

    public void writeBlockuses() throws IOException {
        Files.copy(Paths.get(blockusFile.getPath()), Paths.get(blockusFile.getPath() + ".end"),
                StandardCopyOption.REPLACE_EXISTING);
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
