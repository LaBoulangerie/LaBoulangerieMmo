package net.laboulangerie.laboulangeriemmo.reader;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class BonusYmlReader {

    private File file;
    private YamlConfiguration config;
    private LaBoulangerieMmo plugin;

    public BonusYmlReader(String fileName, LaBoulangerieMmo plugin) {
        plugin.saveResource("bonus.yml", false);
        this.file = new File(plugin.getDataFolder(), fileName);
        config = YamlConfiguration.loadConfiguration(file);
        this.plugin = plugin;
    }

    public double getSumIntList(String path) {
        plugin.getServer().broadcastMessage(path);

        double nbr = 1;
        List<Double> listInt = config.getDoubleList(path);
        for (double mul : listInt) { nbr += mul; }

        plugin.getServer().broadcastMessage(nbr + " ");

        return nbr;
    }
}
