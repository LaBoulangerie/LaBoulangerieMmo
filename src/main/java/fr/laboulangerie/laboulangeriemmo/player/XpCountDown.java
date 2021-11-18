package fr.laboulangerie.laboulangeriemmo.player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.events.XpCountDownFinishedEvent;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;
import net.kyori.adventure.text.Component;

public class XpCountDown extends BukkitRunnable {

    private HashMap<String, Integer> talentToCountDown = new HashMap<String, Integer>();
    private HashMap<String, Double> talentToXp = new HashMap<String, Double>();
    private MmoPlayer player;

    public XpCountDown(MmoPlayer player) {
        this.player = player;
    }

    @Override
    public void run() {
        talentToCountDown.keySet().forEach(key -> {
            talentToCountDown.put(key, talentToCountDown.get(key)-1);

            if (talentToCountDown.get(key) == 0) {
                Talent talent = player.getTalent(key);
                Bukkit.getPlayer(player.getUniqueId())
                    .sendMessage(Component.text("§aVous avez gagné §e" + talentToXp.get(key) + "§axp en §e"
                        + talent.getDisplayName()));
                talentToXp.put(key, 0.0);
                Bukkit.getPluginManager().callEvent(new XpCountDownFinishedEvent(talent, player, talentToXp.get(key)));
            }
        });
    }
    public void startCountDown(String talentId, double xpAmount) {
        talentToCountDown.put(talentId, 4);
        talentToXp.put(talentId, talentToXp.getOrDefault(talentId, 0.0) + xpAmount);
    }
    public void start() {
        runTaskTimerAsynchronously(LaBoulangerieMmo.PLUGIN, 0, 20);
    }
}
