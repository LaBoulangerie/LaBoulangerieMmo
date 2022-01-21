package fr.laboulangerie.laboulangeriemmo;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.laboulangerie.laboulangeriemmo.blockus.BlockusDataManager;
import fr.laboulangerie.laboulangeriemmo.blockus.BlockusListener;
import fr.laboulangerie.laboulangeriemmo.blockus.BlockusRestoration;
import fr.laboulangerie.laboulangeriemmo.commands.MmoCommand;
import fr.laboulangerie.laboulangeriemmo.commands.Stats;
import fr.laboulangerie.laboulangeriemmo.core.Bar;
import fr.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import fr.laboulangerie.laboulangeriemmo.expansions.MmoExpansion;
import fr.laboulangerie.laboulangeriemmo.json.GsonSerializer;
import fr.laboulangerie.laboulangeriemmo.listener.MmoListener;
import fr.laboulangerie.laboulangeriemmo.listener.ServerListener;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayerListener;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayerManager;
import fr.laboulangerie.laboulangeriemmo.player.SkillListener;
import fr.laboulangerie.laboulangeriemmo.player.ability.AbilitiesManager;
import net.milkbowl.vault.economy.Economy;

public class LaBoulangerieMmo extends JavaPlugin {
    public static LaBoulangerieMmo PLUGIN;
    public static Economy ECONOMY = null;
    public static double XP_MULTIPLIER = 0.1;

    private GsonSerializer serializer;
    private BlockusDataManager blockusDataManager;
    private MmoPlayerManager mmoPlayerManager;
    private Bar bar;

    @Override
    public void onEnable() {
        LaBoulangerieMmo.PLUGIN = this;
        this.saveDefaultConfig();
        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE, "Can't load the plugin, Vault isn't present");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.serializer = new GsonSerializer();

        this.blockusDataManager = new BlockusDataManager(this.getDataFolder().getPath() + "/blockus/blockus.dat");
        this.mmoPlayerManager = new MmoPlayerManager();
        this.bar = new Bar();

        BlockusRestoration blockusRestoration = new BlockusRestoration();
        blockusRestoration.runTaskLater(this, 20);

        this.registerListeners();
        getCommand("stats").setExecutor(new Stats());
        getCommand("mmo").setExecutor(new MmoCommand());

        EffectRegistry.registerParticlesEffects();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MmoExpansion().register();
        }

        getLogger().info("Plugin started");
    }

    @Override
    public void onDisable() {
        try {
            this.blockusDataManager.writeBlockuses();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mmoPlayerManager.savePlayersData();
        getLogger().info("Plugin Disabled");
    }

    public GsonSerializer getSerializer() {
        return serializer;
    }

    private void registerListeners() {
        Arrays.asList(
                new ServerListener(),
                new MmoPlayerListener(),
                new SkillListener(),
                new AbilitiesManager(),
                new MmoListener(bar),
                new BlockusListener()).forEach(l -> this.getServer().getPluginManager().registerEvents(l, this));
    }

    public MmoPlayerManager getMmoPlayerManager() {
        return mmoPlayerManager;
    }

    public BlockusDataManager getBlockusDataManager() {
        return blockusDataManager;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        ECONOMY = rsp.getProvider();
        return ECONOMY != null;
    }
}
