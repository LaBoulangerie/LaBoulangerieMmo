package net.laboulangerie.laboulangeriemmo.api.xpboost;

import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class XpBoostObj {


    private final UUID uid;
    private boolean showAlreadyBossBar = false;
    private final KeyedBossBar bossBar;
    public MmoPlayer author;
    public TalentArchetype talent;
    public double boost;
    public int time;
    public int idSchedule = -1;
    public NamespacedKey boosBarKey;

    public XpBoostObj(MmoPlayer author, TalentArchetype talent, double boost, int time) {
        this.uid = UUID.randomUUID();
        this.author = author;
        this.talent = talent;
        this.boost = boost;
        this.time = time;
        int totalTime = this.time;
        final XpBoostObj instance = this;
        this.boosBarKey = new NamespacedKey("laboulangerie_xpboost", this.uid.toString());
        this.bossBar = Bukkit.createBossBar(boosBarKey, updateTitle(), BarColor.GREEN, BarStyle.SOLID);
        this.bossBar.setProgress(1.0D);
        idSchedule = Bukkit.getScheduler().scheduleSyncRepeatingTask(LaBoulangerieMmo.PLUGIN, () -> {
            if(instance.time <= 0) {
                stopBoost();
            }
            if(!this.showAlreadyBossBar) {
                for(Player p : Bukkit.getOnlinePlayers())
                    this.bossBar.addPlayer(p);
                this.showAlreadyBossBar = true;
            }else{
                updateTitle();
                double progress = (double) this.time / totalTime;
                this.bossBar.setProgress(progress);
            }
            instance.time--;
        }, 20, 20L);
    }

    public void stopBoost(){
        this.bossBar.removeAll();
        Bukkit.removeBossBar(boosBarKey);
        Bukkit.getScheduler().cancelTask(idSchedule);
        LaBoulangerieMmo.PLUGIN.getXpBoostManager().expire(this.uid);
    }

    public String updateTitle(){
        int hours = this.time / 3600;
        int minutes = (this.time % 3600) / 60;
        int seconds = this.time % 60;

        String timeString = "err";
        if(hours > 0)
            timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else if(minutes > 0)
            timeString = String.format("%02d:%02d", minutes, seconds);
        else if(seconds > 0)
            timeString = String.format("00:%02d", seconds);

        DecimalFormat df = new DecimalFormat("###.#");
        String newTitle = "XP Boost x"+df.format(this.getBoost())+" - "+talent.displayName+" - "+this.author.getName()+" - Temps restant: "+timeString;
        if(this.bossBar != null)
            this.bossBar.setTitle(newTitle);
        return newTitle;
    }

    public MmoPlayer getAuthor() {
        return author;
    }

    public TalentArchetype getTalent() {
        return talent;
    }

    public double getBoost() {
        return boost;
    }

    public double getTime() {
        return time;
    }

    public UUID getUid() {
        return uid;
    }

    public int getIdSchedule() {
        return idSchedule;
    }

    public KeyedBossBar getBossBar() {
        return bossBar;
    }

}
