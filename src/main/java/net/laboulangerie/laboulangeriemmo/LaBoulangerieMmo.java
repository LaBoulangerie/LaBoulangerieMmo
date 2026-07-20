package net.laboulangerie.laboulangeriemmo;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.BetonQuestApiService;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilitiesRegistry;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayerListener;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayerManager;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentsRegistry;
import net.laboulangerie.laboulangeriemmo.api.xpboost.XpBoostManager;
import net.laboulangerie.laboulangeriemmo.betonquest.LevelConditionFactory;
import net.laboulangerie.laboulangeriemmo.betonquest.XpActionFactory;
import net.laboulangerie.laboulangeriemmo.commands.Combo;
import net.laboulangerie.laboulangeriemmo.commands.LbmmoCommand;
import net.laboulangerie.laboulangeriemmo.commands.MmoCommand;
import net.laboulangerie.laboulangeriemmo.commands.Stats;
import net.laboulangerie.laboulangeriemmo.commands.TownyMmo;
import net.laboulangerie.laboulangeriemmo.commands.talenttree.TalentTree;
import net.laboulangerie.laboulangeriemmo.core.abilities.AbilitiesDispatcher;
import net.laboulangerie.laboulangeriemmo.core.abilities.mining.LuckyVeinListener;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.MobHeadsRegistry;
import net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting.NatureShelter;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusDataManager;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusListener;
import net.laboulangerie.laboulangeriemmo.core.blockus.BlockusManager;
import net.laboulangerie.laboulangeriemmo.core.blockus.redis.RedisBlockusHolder;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboDispatcher;
import net.laboulangerie.laboulangeriemmo.core.json.GsonSerializer;
import net.laboulangerie.laboulangeriemmo.core.mapleaderboard.LeaderBoardManager;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.core.protection.AllowAllTalentProtection;
import net.laboulangerie.laboulangeriemmo.core.protection.FireBowIgniteListener;
import net.laboulangerie.laboulangeriemmo.core.protection.MineLetTalentProtection;
import net.laboulangerie.laboulangeriemmo.core.protection.TalentProtection;
import net.laboulangerie.laboulangeriemmo.core.rareloot.RareLootManager;
import net.laboulangerie.laboulangeriemmo.expansions.MmoExpansion;
import net.laboulangerie.laboulangeriemmo.listener.AbilitiesRegisterer;
import net.laboulangerie.laboulangeriemmo.listener.GrindingListener;
import net.laboulangerie.laboulangeriemmo.listener.MmoListener;
import net.laboulangerie.laboulangeriemmo.listener.RareLootListener;
import net.laboulangerie.laboulangeriemmo.listener.ServerListener;
import net.laboulangerie.laboulangeriemmo.listener.XpBoostListener;
import net.laboulangerie.laboulangeriemmo.utils.WolrdGuardSupport;
import net.milkbowl.vault.economy.Economy;

public class LaBoulangerieMmo extends JavaPlugin {
    public static LaBoulangerieMmo PLUGIN;
    public static Economy ECONOMY = null;
    public static double XP_MULTIPLIER = 0.1;
    public static TalentsRegistry talentsRegistry = null;
    public static AbilitiesRegistry abilitiesRegistry = null;
    public static boolean WORLDGUARD_SUPPORT = false;
    public static boolean MYTHICMOBS_SUPPORT = false;
    public static boolean LIBSDISGUISES_SUPPORT = false;
    public static DecimalFormat formatter;
    public static int COMBO_LENGTH = 3;

    private GsonSerializer serializer;
    private BlockusManager blockusDataManager;
    private RedisBlockusHolder blockusHolder;
    private MmoPlayerManager mmoPlayerManager;
    private TalentProtection talentProtection = new AllowAllTalentProtection();

    private XpBoostManager xpBoostManager;
    private RareLootManager rareLootManager;

    @Override
    public void onLoad() {
        LaBoulangerieMmo.PLUGIN = this;
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            WolrdGuardSupport.enableSupport();
            WORLDGUARD_SUPPORT = true;
            getLogger().info("Hooked into WorldGuard!");
        }

        this.blockusHolder = new RedisBlockusHolder();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadNumberFormatter();
        setupMineLetProtection();
        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE, "Can't load the plugin, Vault isn't present");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (getServer().getPluginManager().getPlugin("MythicMobs") != null) {
            MYTHICMOBS_SUPPORT = true;
            getLogger().info("Hooked into MythicMobs!");
        }
        if (getServer().getPluginManager().getPlugin("LibsDisguises") != null) {
            LIBSDISGUISES_SUPPORT = true;
            getLogger().info("Hooked into LibsDisguises!");
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
        rareLootManager = new RareLootManager(this);
        rareLootManager.createDefaults();
        rareLootManager.reload();

        registerListeners();
        getCommand("stats").setExecutor(new Stats());
        getCommand("mmo").setExecutor(new MmoCommand());
        getCommand("combo").setExecutor(new Combo());
        getCommand("talent").setExecutor(new TalentTree());
        LbmmoCommand lbmmoCommand = new LbmmoCommand();
        getCommand("lbmmo").setExecutor(lbmmoCommand);
        getCommand("lbmmo").setTabCompleter(lbmmoCommand);

        EffectRegistry.registerParticlesEffects();

        // Initialize ability systems
        MobHeadsRegistry.loadHeads();
        NatureShelter.startTask();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MmoExpansion().register();
        }
        if (getServer().getPluginManager().getPlugin("Towny") != null) {
            getCommand("townymmo").setExecutor(new TownyMmo());
        }

        if (getServer().getPluginManager().getPlugin("BetonQuest") != null) {
            BetonQuestApiService.get().ifPresentOrElse(
                service -> {
                    BetonQuestApi api = service.api(this);
                    api.conditions().registry().register("lbmmo_level", new LevelConditionFactory());
                    api.actions().registry().register("lbmmo_xp", new XpActionFactory());
                    getLogger().info("Hooked into BetonQuest 3.0!");
                },
                () -> getLogger().warning("BetonQuest found but API not available")
            );
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
        if (mmoPlayerManager != null)
            mmoPlayerManager.savePlayersData();
        getLogger().info("Plugin Disabled");
    }

    public GsonSerializer getSerializer() {
        return serializer;
    }

    private void registerListeners() {
        Arrays.asList(new ServerListener(), new MmoPlayerListener(), new GrindingListener(), new AbilitiesDispatcher(),
                new MmoListener(), new BlockusListener(), new XpBoostListener(), LeaderBoardManager.getInstance(),
                new ComboDispatcher(), new LuckyVeinListener(), new FireBowIgniteListener(),
                new RareLootListener(rareLootManager.engine()))
                .forEach(l -> getServer().getPluginManager().registerEvents(l, this));
    }

    private void setupMineLetProtection() {
        if (getServer().getPluginManager().getPlugin("Minelet") == null) {
            talentProtection = new AllowAllTalentProtection(this, "MINELET_PLUGIN_ABSENT");
            getLogger().info("[MineLetProtection] mode=PERMISSIVE reason=MINELET_PLUGIN_ABSENT");
            return;
        }

        MineLetTalentProtection mineLetProtection = MineLetTalentProtection.create(this);
        if (mineLetProtection == null) {
            talentProtection = new AllowAllTalentProtection(this, "MINELET_API_UNAVAILABLE");
            getLogger().warning("[MineLetProtection] mode=PERMISSIVE reason=MINELET_API_UNAVAILABLE");
            return;
        }

        talentProtection = mineLetProtection;
        getLogger().info("[MineLetProtection] mode=ACTIVE version=" + mineLetProtection.getVersion());
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

    public RedisBlockusHolder getBlockusHolder() {
        return blockusHolder;
    }

    public RareLootManager.ReloadResult reloadRuntimeConfiguration() {
        reloadConfig();
        reloadNumberFormatter();
        abilitiesRegistry.init();
        talentsRegistry.init();
        mmoPlayerManager.stream().forEach(MmoPlayer::postProcess);
        MobHeadsRegistry.loadHeads();
        return rareLootManager.reload();
    }

    public RareLootManager getRareLootManager() {
        return rareLootManager;
    }

    public TalentProtection getTalentProtection() {
        return talentProtection;
    }

    private void reloadNumberFormatter() {
        String locale = getConfig().getString("locale", "en_UK");
        formatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.forLanguageTag(locale.replace('_', '-')));
        formatter.applyPattern("#.##");
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
