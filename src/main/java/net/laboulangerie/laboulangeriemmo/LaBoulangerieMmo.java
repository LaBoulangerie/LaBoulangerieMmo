package net.laboulangerie.laboulangeriemmo;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilitiesRegistry;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayerListener;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayerManager;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentsRegistry;
import net.laboulangerie.laboulangeriemmo.api.xpboost.XpBoostManager;
import net.laboulangerie.laboulangeriemmo.betonquest.LevelCondition;
import net.laboulangerie.laboulangeriemmo.betonquest.XpEvent;
import net.laboulangerie.laboulangeriemmo.commands.Combo;
import net.laboulangerie.laboulangeriemmo.commands.MmoCommand;
import net.laboulangerie.laboulangeriemmo.commands.Stats;
import net.laboulangerie.laboulangeriemmo.commands.TownyMmo;
import net.laboulangerie.laboulangeriemmo.commands.talenttree.TalentTree;
import net.laboulangerie.laboulangeriemmo.core.abilities.AbilitiesDispatcher;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusDataManager;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusListener;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusManager;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusRestoration;
import net.laboulangerie.laboulangeriemmo.core.blockus.redis.RedisBlockusHolder;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboDispatcher;
import net.laboulangerie.laboulangeriemmo.core.mapleaderboard.LeaderBoardManager;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.expansions.MmoExpansion;
import net.laboulangerie.laboulangeriemmo.json.GsonSerializer;
import net.laboulangerie.laboulangeriemmo.listener.AbilitiesRegisterer;
import net.laboulangerie.laboulangeriemmo.listener.GrindingListener;
import net.laboulangerie.laboulangeriemmo.listener.MmoListener;
import net.laboulangerie.laboulangeriemmo.listener.ServerListener;
import net.laboulangerie.laboulangeriemmo.listener.XpBoostListener;
import net.laboulangerie.laboulangeriemmo.utils.WolrdGuardSupport;
import net.milkbowl.vault.economy.Economy;
import pl.betoncraft.betonquest.BetonQuest;

public class LaBoulangerieMmo extends JavaPlugin {
    public static LaBoulangerieMmo PLUGIN;
    public static Economy ECONOMY = null;
    public static double XP_MULTIPLIER = 0.1;
    public static TalentsRegistry talentsRegistry = null;
    public static AbilitiesRegistry abilitiesRegistry = null;
    public static boolean WORLDGUARD_SUPPORT = false;
    public static boolean MYTHICMOBS_SUPPORT = false;
    public static DecimalFormat formatter;
    public static int COMBO_LENGTH = 3;

    private GsonSerializer serializer;
    private BlockusManager blockusDataManager;
    private RedisBlockusHolder blockusHolder = new RedisBlockusHolder();
    private MmoPlayerManager mmoPlayerManager;

    private XpBoostManager xpBoostManager;

    @Override
    public void onLoad() {
        LaBoulangerieMmo.PLUGIN = this;
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            WolrdGuardSupport.enableSupport();
            WORLDGUARD_SUPPORT = true;
            getLogger().info("Hooked into WorldGuard!");
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        formatter =
                (DecimalFormat) NumberFormat.getNumberInstance(Locale.forLanguageTag(getConfig().getString("locale")));
        formatter.applyPattern("#.##");
        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE, "Can't load the plugin, Vault isn't present");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (getServer().getPluginManager().getPlugin("MythicMobs") != null) {
            MYTHICMOBS_SUPPORT = true;
            getLogger().info("Hooked into MythicMobs!");
        }

        serializer = new GsonSerializer();

        LaBoulangerieMmo.abilitiesRegistry = new AbilitiesRegistry();
        getServer().getPluginManager().registerEvents(new AbilitiesRegisterer(), this);
        abilitiesRegistry.init();

        LaBoulangerieMmo.talentsRegistry = new TalentsRegistry();
        talentsRegistry.init();

        blockusDataManager = new BlockusDataManager(getDataFolder().getPath() + "/blockus/blockus.dat");
        mmoPlayerManager = new MmoPlayerManager();
        xpBoostManager = new XpBoostManager();

        BlockusRestoration blockusRestoration = new BlockusRestoration();
        blockusRestoration.runTaskLater(this, 20);

        registerListeners();
        getCommand("stats").setExecutor(new Stats());
        getCommand("mmo").setExecutor(new MmoCommand());
        getCommand("combo").setExecutor(new Combo());
        getCommand("talent").setExecutor(new TalentTree());

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
        if (blockusDataManager != null) {
            try {
                blockusDataManager.writeBlockuses();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mmoPlayerManager != null) mmoPlayerManager.savePlayersData();
        getLogger().info("Plugin Disabled");
    }

    public GsonSerializer getSerializer() {
        return serializer;
    }

    private void registerListeners() {
        Arrays.asList(new ServerListener(), new MmoPlayerListener(), new GrindingListener(), new AbilitiesDispatcher(),
                new MmoListener(), new BlockusListener(), new XpBoostListener(), LeaderBoardManager.getInstance(),
                new ComboDispatcher()).forEach(l -> getServer().getPluginManager().registerEvents(l, this));
    }

    public MmoPlayerManager getMmoPlayerManager() {
        return mmoPlayerManager;
    }

    public XpBoostManager getXpBoostManager() {
        return xpBoostManager;
    }

    public BlockusManager getBlockusDataManager() {
        return blockusDataManager;
    }

    public RedisBlockusHolder getBlockusHolder() { return blockusHolder; }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        ECONOMY = rsp.getProvider();
        return ECONOMY != null;
    }
}
