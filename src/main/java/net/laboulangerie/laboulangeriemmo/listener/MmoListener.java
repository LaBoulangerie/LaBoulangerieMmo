package net.laboulangerie.laboulangeriemmo.listener;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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

        List<TagResolver.Single> placeholders = Arrays.asList(
            Placeholder.parsed("level", Integer.toString(talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER))),
            Placeholder.parsed("talent", talent.getDisplayName()),
            Placeholder.parsed("reward", "1000$") // TODO Changer "1000$"
        );
        
        player.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"))
                .append(MiniMessage.miniMessage().deserialize(config.getString("lang.messages.level_up"), TagResolver.resolver(placeholders))));
    }

    @EventHandler
    public void onCountDownFinished(XpCountDownFinishedEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        bar.displayBar(event.getTalent(), event.getPlayer());

        List<TagResolver.Single> placeholders = Arrays.asList(
            Placeholder.parsed("xp", Double.toString(event.getAmount())),
            Placeholder.parsed("talent", event.getTalent().getDisplayName())
        );

        player.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"))
                .append(MiniMessage.miniMessage().deserialize(config.getString("lang.messages.xp_up"), TagResolver.resolver(placeholders))));
    }
}
