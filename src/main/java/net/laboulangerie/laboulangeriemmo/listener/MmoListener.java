package net.laboulangerie.laboulangeriemmo.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
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
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype;
import net.laboulangerie.laboulangeriemmo.api.xpboost.XpBoostObj;
import net.laboulangerie.laboulangeriemmo.core.XpBar;
import net.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
import net.laboulangerie.laboulangeriemmo.events.XpCountDownFinishedEvent;
import net.milkbowl.vault.economy.EconomyResponse;

public class MmoListener implements Listener {

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId());
        Player player = offlinePlayer.getPlayer();
        Talent talent = event.getTalent();
        TalentArchetype talentArchetype = LaBoulangerieMmo.talentsRegistry.getTalent(talent.getTalentId());

        List<TagResolver.Single> resolvers = new ArrayList<>();
        resolvers.add(Placeholder.parsed("level", Integer.toString(event.getNewLevel())));
        resolvers.add(Placeholder.parsed("talent", talent.getDisplayName()));

        ConfigurationSection rewards =
                config.getConfigurationSection("level-up-rewards." + talent.getTalentId());
        double amount = calculateLevelRewards(
                rewards, event.getPreviousLevel(), event.getNewLevel(), config.getString("rewards-rounding-method", "no"));
        boolean rewardPaid = false;
        if (amount > 0) {
            EconomyResponse response = LaBoulangerieMmo.ECONOMY.depositPlayer(offlinePlayer, amount);
            if (response.transactionSuccess()) {
                rewardPaid = true;
                resolvers.add(Placeholder.parsed("reward", LaBoulangerieMmo.ECONOMY.format(amount)));
            } else {
                LaBoulangerieMmo.PLUGIN.getLogger().warning("Unable to pay " + amount + " to "
                        + offlinePlayer.getName() + " for " + talent.getTalentId() + " levels "
                        + (event.getPreviousLevel() + 1) + "-" + event.getNewLevel() + ": " + response.errorMessage);
            }
        }

        if (player == null) return;
        Component prefix = MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"));

        // Check for unlocked ability tiers
        if (talentArchetype != null) {
            for (AbilityArchetype abilityArchetype : talentArchetype.abilitiesArchetypes.values()) {
                List<TagResolver.Single> abilityResolvers = new ArrayList<>(resolvers);
                abilityResolvers.add(Placeholder.parsed("ability", abilityArchetype.displayName));

                if (!abilityArchetype.hasTiers() && crossedLevel(event, abilityArchetype.requiredLevel)) {
                    sendAbilityMessage(player, prefix, config.getString("lang.messages.ability-unlocked"),
                            abilityResolvers);
                    continue;
                }

                for (int i = 0; i < abilityArchetype.tiers.size(); i++) {
                    if (!crossedLevel(event, abilityArchetype.tiers.get(i))) continue;
                    List<TagResolver.Single> tierResolvers = new ArrayList<>(abilityResolvers);
                    tierResolvers.add(Placeholder.parsed("tier", Integer.toString(i + 1)));
                    String message = config.getString(
                            i == 0 ? "lang.messages.ability-unlocked" : "lang.messages.ability-upgrade");
                    sendAbilityMessage(player, prefix, message, tierResolvers);
                }
            }
        }

        String levelMessage = rewardPaid ? "lang.messages.level-up" : "lang.messages.level-up-no-reward";
        Component lvlUpComponent = MiniMessage.miniMessage()
                .deserialize(config.getString(levelMessage), TagResolver.resolver(resolvers));
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
        FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        if (player == null) return;
        XpBar.displayBar(event.getTalent(), event.getPlayer());

        List<TagResolver.Single> placeholders =
                Arrays.asList(Placeholder.parsed("xp", LaBoulangerieMmo.formatter.format(event.getAmount())),
                        Placeholder.parsed("talent", event.getTalent().getDisplayName()));

        Component message = MiniMessage.miniMessage().deserialize(config.getString("lang.messages.xp_up"),
                TagResolver.resolver(placeholders));

        XpBoostObj xpBoost = LaBoulangerieMmo.PLUGIN.getXpBoostManager().getBoost(event.getTalent().getTalentId());

        if (xpBoost != null) {
            TagResolver.Single boostPlaceholder = Placeholder.parsed("boost", xpBoost.getFormattedBoost());
            Component boostMessage = MiniMessage.miniMessage()
                    .deserialize(config.getString("lang.messages.xp_up_boost"), boostPlaceholder);
            message = message.append(boostMessage);
        }

        player.sendActionBar(message);
    }

    static double calculateLevelReward(ConfigurationSection rewards, int level, double levelXp) {
        if (rewards == null || level <= 0) return 0;

        double amount = processMoneyAmount(rewards.getString("*"), levelXp);
        amount += processMoneyAmount(rewards.getString(Integer.toString(level)), levelXp);

        ConfigurationSection progressive = rewards.getConfigurationSection("progressive");
        if (progressive == null || !progressive.isSet("levels-per-step")
                || !progressive.isSet("amount-per-step")) {
            return amount;
        }

        int levelsPerStep = progressive.getInt("levels-per-step");
        double amountPerStep = progressive.getDouble("amount-per-step");
        if (levelsPerStep <= 0 || amountPerStep <= 0) return amount;

        return amount + ((level / levelsPerStep) + 1) * amountPerStep;
    }

    static double calculateLevelRewards(
            ConfigurationSection rewards, int previousLevel, int newLevel, String roundingMethod) {
        return calculateLevelRewards(
                rewards, previousLevel, newLevel, roundingMethod, LaBoulangerieMmo.XP_MULTIPLIER);
    }

    static double calculateLevelRewards(ConfigurationSection rewards, int previousLevel, int newLevel,
            String roundingMethod, double xpMultiplier) {
        if (newLevel <= previousLevel) return 0;
        if (xpMultiplier <= 0 || !Double.isFinite(xpMultiplier)) return 0;

        double total = 0;
        for (int level = Math.max(1, previousLevel + 1); level <= newLevel; level++) {
            double levelXp = Math.pow(level / xpMultiplier, 2);
            total += roundReward(calculateLevelReward(rewards, level, levelXp), roundingMethod);
        }
        return total;
    }

    private static double roundReward(double amount, String method) {
        if (method == null) return amount;
        return switch (method) {
            case "closest" -> Math.round(amount);
            case "up" -> Math.ceil(amount);
            case "down" -> Math.floor(amount);
            default -> amount;
        };
    }

    private static boolean crossedLevel(PlayerLevelUpEvent event, int requiredLevel) {
        return requiredLevel > event.getPreviousLevel() && requiredLevel <= event.getNewLevel();
    }

    private static void sendAbilityMessage(Player player, Component prefix, String message,
            List<TagResolver.Single> resolvers) {
        Component component = MiniMessage.miniMessage().deserialize(message, TagResolver.resolver(resolvers));
        player.sendMessage(prefix.append(component));
    }

    private static double processMoneyAmount(String rawAmount, double levelXp) {
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
