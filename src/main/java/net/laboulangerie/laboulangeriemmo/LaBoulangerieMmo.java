package net.laboulangerie.laboulangeriemmo;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilitiesRegistry;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayerListener;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayerManager;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentsRegistry;
import net.laboulangerie.laboulangeriemmo.betonquest.LevelCondition;
import net.laboulangerie.laboulangeriemmo.betonquest.XpEvent;
import net.laboulangerie.laboulangeriemmo.commands.Combo;
import net.laboulangerie.laboulangeriemmo.commands.MmoCommand;
import net.laboulangerie.laboulangeriemmo.commands.Stats;
import net.laboulangerie.laboulangeriemmo.commands.TownyMmo;
import net.laboulangerie.laboulangeriemmo.core.abilities.AbilitiesDispatcher;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusDataManager;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusListener;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusRestoration;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboDispatcher;
import net.laboulangerie.laboulangeriemmo.core.mapleaderboard.LeaderBoardManager;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.expansions.MmoExpansion;
import net.laboulangerie.laboulangeriemmo.json.GsonSerializer;
import net.laboulangerie.laboulangeriemmo.listener.AbilitiesRegisterer;
import net.laboulangerie.laboulangeriemmo.listener.GrindingListener;
import net.laboulangerie.laboulangeriemmo.listener.MmoListener;
import net.laboulangerie.laboulangeriemmo.listener.ServerListener;
import net.milkbowl.vault.economy.Economy;
import pl.betoncraft.betonquest.BetonQuest;

public class LaBoulangerieMmo extends JavaPlugin {
    public static LaBoulangerieMmo PLUGIN;
    public static Economy ECONOMY = null;
    public static double XP_MULTIPLIER = 0.1;
    public static TalentsRegistry talentsRegistry = null;
    public static AbilitiesRegistry abilitiesRegistry = null;

    private GsonSerializer serializer;
    private BlockusDataManager blockusDataManager;
    private MmoPlayerManager mmoPlayerManager;

    @Override
    public void onEnable() {
        LaBoulangerieMmo.PLUGIN = this;
        saveDefaultConfig();
        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE, "Can't load the plugin, Vault isn't present");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        serializer = new GsonSerializer();

        LaBoulangerieMmo.abilitiesRegistry = new AbilitiesRegistry();
        getServer().getPluginManager().registerEvents(new AbilitiesRegisterer(), this);
        abilitiesRegistry.init();

        LaBoulangerieMmo.talentsRegistry = new TalentsRegistry();
        talentsRegistry.init();

        blockusDataManager = new BlockusDataManager(getDataFolder().getPath() + "/blockus/blockus.dat");
        mmoPlayerManager = new MmoPlayerManager();

        BlockusRestoration blockusRestoration = new BlockusRestoration();
        blockusRestoration.runTaskLater(this, 20);

        registerListeners();
        getCommand("stats").setExecutor(new Stats());
        getCommand("mmo").setExecutor(new MmoCommand());
        getCommand("combo").setExecutor(new Combo());

        EffectRegistry.registerParticlesEffects();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MmoExpansion().register();
        }
        if (getServer().getPluginManager().getPlugin("Towny") != null) {
            getCommand("townymmo").setExecutor(new TownyMmo());
        }

        if (getServer().getPluginManager().getPlugin("BetonQuest") != null) {
            BetonQuest.getInstance().registerConditions("lbmmo_level", LevelCondition.class);
            BetonQuest.getInstance().registerEvents("lbmmo_xp", XpEvent.class);
            getLogger().info("Hooked in BetonQuest!"); 
        }

        getLogger().info("Plugin started");
    }

    @Override
    public void onDisable() {
        try {
            blockusDataManager.writeBlockuses();
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
                new GrindingListener(),
                new AbilitiesDispatcher(),
                new MmoListener(),
                new BlockusListener(),
                LeaderBoardManager.getInstance(),
                new ComboDispatcher()).forEach(l -> getServer().getPluginManager().registerEvents(l, this));
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
