package fr.laboulangerie.laboulangeriemmo;

import fr.laboulangerie.laboulangeriemmo.blockus.BlockusDataManager;
import fr.laboulangerie.laboulangeriemmo.json.GsonSerializer;
import fr.laboulangerie.laboulangeriemmo.listener.ServerListener;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;

public class LaBoulangerieMmo extends JavaPlugin {

    private GsonSerializer serializer;

    private BlockusDataManager blockusDataManager;
    private MmoPlayerManager mmoPlayerManager;

    @Override
    public void onEnable() {
        this.serializer = new GsonSerializer();

        this.blockusDataManager = new BlockusDataManager(this.getDataFolder().getPath() + "/blockus/blockus.dat");
        this.mmoPlayerManager = new MmoPlayerManager(this);

        this.registerListeners();
    }

    @Override
    public void onDisable() {
        try {
            this.blockusDataManager.writeBlockuses();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GsonSerializer getSerializer() {
        return serializer;
    }


    private void registerListeners() {
        Arrays.asList(
               new ServerListener(this)
        ).forEach(l->this.getServer().getPluginManager().registerEvents(l, this));
    }

    public MmoPlayerManager getMmoPlayerManager() {
        return mmoPlayerManager;
    }

    public BlockusDataManager getBlockusDataManager() {
        return blockusDataManager;
    }
}
