package net.laboulangerie.laboulangeriemmo.api.xpboost;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class XpBoostObj {

    private final UUID uid;
    private boolean initShowBossBar = true;
    private boolean shownBar = false;
    private final BossBar bossBar;
    public MmoPlayer author;
    public TalentArchetype talent;
    public double boost;
    public int time;
    public int idSchedule = -1;

    public XpBoostObj(MmoPlayer author, TalentArchetype talent, double boost, int time) {
        this.uid = UUID.randomUUID();
        this.author = author;
        this.talent = talent;
        this.boost = boost;
        this.time = time;
        this.bossBar = BossBar.bossBar(updateTitle(), 1, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);
    }

    public void startBoost() {
        if (this.initShowBossBar) {
            this.shownBar = true;
            for (Player p : Bukkit.getOnlinePlayers())
                p.showBossBar(this.bossBar);
        }
        int totalTime = this.time;
        idSchedule = Bukkit.getScheduler().scheduleSyncRepeatingTask(LaBoulangerieMmo.PLUGIN, () -> {
            if (this.time <= 0) {
                stopBoost();
            }
            updateTitle();

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            float progress = Float
                    .parseFloat(df.format((float) this.time / totalTime).replace(",", "."));
            this.bossBar.progress(progress);

            this.time--;
        }, 20, 20L);
    }

    public void stopBoost() {
        for (Player p : Bukkit.getOnlinePlayers())
            p.hideBossBar(this.bossBar);
        Bukkit.getScheduler().cancelTask(idSchedule);
        LaBoulangerieMmo.PLUGIN.getXpBoostManager().expire(this.uid);
    }

    public Component updateTitle() {
        int hours = this.time / 3600;
        int minutes = (this.time % 3600) / 60;
        int seconds = this.time % 60;

        String timeString = "err";
        if (hours > 0)
            timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else if (minutes > 0)
            timeString = String.format("%02d:%02d", minutes, seconds);
        else if (seconds > 0)
            timeString = String.format("00:%02d", seconds);

        FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();

        List<TagResolver.Single> placeholders =
                Arrays.asList(Placeholder.parsed("boost", this.getFormattedBoost()),
                        Placeholder.parsed("talent", talent.displayName),
                        Placeholder.parsed("author", this.author.getName()),
                        Placeholder.parsed("time", timeString));

        Component newTitle = MiniMessage.miniMessage().deserialize(
                config.getString("lang.xp_boost.bar_title"), TagResolver.resolver(placeholders));

        if (this.bossBar != null)
            this.bossBar.name(newTitle);
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

    public String getFormattedBoost() {
        DecimalFormat df = new DecimalFormat("###.#");
        return df.format(this.getBoost());
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

    public BossBar getBossBar() {
        return bossBar;
    }

    public void hideBossBar() {
        this.shownBar = false;
        for (Player p : Bukkit.getOnlinePlayers())
            p.hideBossBar(this.bossBar);
    }

    public void showBossBar() {
        this.shownBar = true;
        for (Player p : Bukkit.getOnlinePlayers())
            p.showBossBar(this.bossBar);
    }

    public void setInitShowBossBar(boolean initShownBossBarBar) {
        this.initShowBossBar = initShownBossBarBar;
    }

    public boolean isShownBar() {
        return shownBar;
    }
}
