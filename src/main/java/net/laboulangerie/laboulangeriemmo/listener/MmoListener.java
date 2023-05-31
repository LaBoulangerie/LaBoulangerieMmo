package net.laboulangerie.laboulangeriemmo.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;
import net.laboulangerie.laboulangeriemmo.api.xpboost.XpBoostObj;
import net.laboulangerie.laboulangeriemmo.core.XpBar;
import net.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
import net.laboulangerie.laboulangeriemmo.events.XpCountDownFinishedEvent;

public class MmoListener implements Listener {
    private FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();

        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        Talent talent = event.getTalent();

        Component prefix = MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"));

        List<TagResolver.Single> resolvers = new ArrayList<>();
        resolvers.add(Placeholder.parsed("level", Integer.toString(talent.getLevel())));
        resolvers.add(Placeholder.parsed("talent", talent.getDisplayName()));

        if (!config.isSet("level-up-rewards." + talent.getTalentId())) {
            Component noRewardComponent = MiniMessage.miniMessage()
                    .deserialize(config.getString("lang.messages.level-up-no-reward"), TagResolver.resolver(resolvers));

            player.sendMessage(prefix.append(noRewardComponent));
            return;
        }

        double amount = processMoneyAmount(config.getString("level-up-rewards." + talent.getTalentId() + ".*"),
                talent.getLevelXp());

        amount += processMoneyAmount(
                config.getString("level-up-rewards." + talent.getTalentId() + "." + talent.getLevel()),
                talent.getLevelXp());

        if (amount == 0) return;
        switch (config.getString("rewards-rounding-method", "no")) {
            case "closest":
                amount = Math.round(amount);
                break;
            case "up":
                amount = Math.ceil(amount);
                break;
            case "down":
                amount = Math.floor(amount);
                break;
            case "no":
            default:
                break;
        }

        LaBoulangerieMmo.ECONOMY.depositPlayer((OfflinePlayer) player, amount);

        resolvers.add(Placeholder.parsed("reward", LaBoulangerieMmo.ECONOMY.format(amount)));

        Component lvlUpComponent = MiniMessage.miniMessage().deserialize(config.getString("lang.messages.level-up"),
                TagResolver.resolver(resolvers));
        player.sendMessage(prefix.append(lvlUpComponent));

        Component titleComponent =
                MiniMessage.miniMessage().deserialize(config.getString("lang.messages.level-up-title"));
        Component subTitleComponent = MiniMessage.miniMessage()
                .deserialize(config.getString("lang.messages.level-up-subtitle"), TagResolver.resolver(resolvers));

        Title lvlUpTitle = Title.title(titleComponent, subTitleComponent);
        player.showTitle(lvlUpTitle);

        Sound lvlUpSound = Sound.sound(Key.key("ui.toast.challenge_complete"), Sound.Source.AMBIENT, 1, 1);
        player.playSound(lvlUpSound);
    }

    @EventHandler
    public void onCountDownFinished(XpCountDownFinishedEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        if (player == null) return;
        XpBar.displayBar(event.getTalent(), event.getPlayer());

        List<TagResolver.Single> placeholders =
                Arrays.asList(Placeholder.parsed("xp", LaBoulangerieMmo.formatter.format(event.getAmount())),
                        Placeholder.parsed("talent", event.getTalent().getDisplayName()));

        Component prefix = MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"));
        Component message = MiniMessage.miniMessage().deserialize(config.getString("lang.messages.xp_up"),
                TagResolver.resolver(placeholders));

        XpBoostObj xpBoost = LaBoulangerieMmo.PLUGIN.getXpBoostManager().getBoost(event.getTalent().getTalentId());

        if (xpBoost != null) {
            TagResolver.Single boostPlaceholder = Placeholder.parsed("boost", xpBoost.getFormattedBoost());
            Component boostMessage = MiniMessage.miniMessage()
                    .deserialize(config.getString("lang.messages.xp_up_boost"), boostPlaceholder);
            message = message.append(boostMessage);
        }

        player.sendMessage(prefix.append(message));
    }

    private double processMoneyAmount(String rawAmount, double levelXp) {
        if (rawAmount == null) return 0;

        if (rawAmount.endsWith("%")) {
            double percentage = 0;
            try {
                percentage = Double.parseDouble(rawAmount.split("%")[0]);
            } catch (Exception e) {
            }
            return levelXp * percentage / 100;
        }
        try {
            return Double.parseDouble(rawAmount);
        } catch (Exception e) {
        }

        return 0;
    }
}
