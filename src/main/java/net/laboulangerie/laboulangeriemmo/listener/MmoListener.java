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
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;
import net.laboulangerie.laboulangeriemmo.core.Bar;
import net.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
import net.laboulangerie.laboulangeriemmo.events.XpCountDownFinishedEvent;

public class MmoListener implements Listener {

    private Bar bar;
    private FileConfiguration config;

    public MmoListener(Bar bar) {
        this.bar = bar;
        config = LaBoulangerieMmo.PLUGIN.getConfig();
    }

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        Talent talent = event.getTalent();

        if (!LaBoulangerieMmo.PLUGIN.getConfig().isSet("level-up-rewards." + talent.getTalentId())) return;

        double amount = LaBoulangerieMmo.PLUGIN.getConfig().getDouble("level-up-rewards."+talent.getTalentId()+".*", 0);
        amount += LaBoulangerieMmo.PLUGIN.getConfig().getDouble("level-up-rewards."+talent.getTalentId()+"."+talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER), 0);

        if (amount == 0) return;
        LaBoulangerieMmo.ECONOMY.depositPlayer((OfflinePlayer) player, amount);

        List<TagResolver.Single> placeholders = Arrays.asList(
            Placeholder.parsed("level", Integer.toString(talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER))),
            Placeholder.parsed("talent", talent.getDisplayName()),
            Placeholder.parsed("reward", amount + "$")
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
