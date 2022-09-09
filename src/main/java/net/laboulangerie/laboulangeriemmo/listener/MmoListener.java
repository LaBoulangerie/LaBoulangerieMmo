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
import net.laboulangerie.laboulangeriemmo.core.XpBar;
import net.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
import net.laboulangerie.laboulangeriemmo.events.XpCountDownFinishedEvent;

public class MmoListener implements Listener {
    private FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        Talent talent = event.getTalent();

        if (!LaBoulangerieMmo.PLUGIN.getConfig().isSet("level-up-rewards." + talent.getTalentId())) {
            List<TagResolver.Single> placeholders = Arrays.asList(
                Placeholder.parsed("level", Integer.toString(talent.getLevel())),
                Placeholder.parsed("talent", talent.getDisplayName())
            );

            player.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"))
                .append(MiniMessage.miniMessage().deserialize(config.getString("lang.messages.level-up-no-rewards"), TagResolver.resolver(placeholders))));
            return;
        }

        double amount = processMoneyAmount(LaBoulangerieMmo.PLUGIN.getConfig().getString("level-up-rewards."+talent.getTalentId()+".*"), talent.getLevelXp());
        amount += processMoneyAmount(LaBoulangerieMmo.PLUGIN.getConfig().getString("level-up-rewards."+talent.getTalentId()+"."+talent.getLevel()), talent.getLevelXp());

        if (amount == 0) return;
        LaBoulangerieMmo.ECONOMY.depositPlayer((OfflinePlayer) player, amount);

        List<TagResolver.Single> placeholders = Arrays.asList(
            Placeholder.parsed("level", Integer.toString(talent.getLevel())),
            Placeholder.parsed("talent", talent.getDisplayName()),
            Placeholder.parsed("reward", LaBoulangerieMmo.formatter.format(amount) + "$")
        );

        player.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"))
                .append(MiniMessage.miniMessage().deserialize(config.getString("lang.messages.level-up"), TagResolver.resolver(placeholders))));
    }

    @EventHandler
    public void onCountDownFinished(XpCountDownFinishedEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        XpBar.displayBar(event.getTalent(), event.getPlayer());

        List<TagResolver.Single> placeholders = Arrays.asList(
            Placeholder.parsed("xp", LaBoulangerieMmo.formatter.format(event.getAmount())),
            Placeholder.parsed("talent", event.getTalent().getDisplayName())
        );

        player.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"))
                .append(MiniMessage.miniMessage().deserialize(config.getString("lang.messages.xp_up"), TagResolver.resolver(placeholders))));
    }

    private double processMoneyAmount(String rawAmount, double levelXp) {
        if (rawAmount == null) return 0;
        System.out.println(levelXp);
        if (rawAmount.endsWith("%")) {
            double percentage = 0;
            try {
                percentage = Double.parseDouble(rawAmount.split("%")[0]);
            } catch (Exception e) {}
            return levelXp * percentage / 100;
        }
        try {
            return Double.parseDouble(rawAmount);
        } catch (Exception e) {}

        return 0;
    }
}
