package net.laboulangerie.laboulangeriemmo.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.Bar;
import net.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
import net.laboulangerie.laboulangeriemmo.events.XpCountDownFinishedEvent;
import net.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class MmoListener implements Listener {

    private Bar bar;
    private FileConfiguration config;

    public MmoListener(Bar bar) {
        this.bar = bar;
        this.config = LaBoulangerieMmo.PLUGIN.getConfig();
    }

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        Talent talent = event.getTalent();
        LaBoulangerieMmo.ECONOMY.depositPlayer((OfflinePlayer) player, 1_000);

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("level", Integer.toString(talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER)));
        placeholders.put("talent", talent.getDisplayName());
        placeholders.put("reward", "1000$"); // TODO Changer "1000$"

        player.sendMessage(MiniMessage.get().parse(config.getString("lang.prefix"))
                .append(MiniMessage.get().parse(config.getString("lang.messages.level_up"), placeholders)));
    }

    @EventHandler
    public void onCountDownFinished(XpCountDownFinishedEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        bar.displayBar(event.getTalent(), event.getPlayer());

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("xp", Double.toString(event.getAmount()));
        placeholders.put("talent", event.getTalent().getDisplayName());

        player.sendMessage(MiniMessage.get().parse(config.getString("lang.prefix"))
                .append(MiniMessage.get().parse(config.getString("lang.messages.xp_up"), placeholders)));
    }
}
