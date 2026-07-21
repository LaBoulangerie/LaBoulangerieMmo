package net.laboulangerie.laboulangeriemmo.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
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
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype;
import net.laboulangerie.laboulangeriemmo.api.xpboost.XpBoostObj;
import net.laboulangerie.laboulangeriemmo.core.XpBar;
import net.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
import net.laboulangerie.laboulangeriemmo.events.XpCountDownFinishedEvent;

public class MmoListener implements Listener {

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();

        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        Talent talent = event.getTalent();
        TalentArchetype talentArchetype = LaBoulangerieMmo.talentsRegistry.getTalent(talent.getTalentId());

        List<TagResolver.Single> resolvers = new ArrayList<>();
        resolvers.add(Placeholder.parsed("level", Integer.toString(event.getNewLevel())));
        resolvers.add(Placeholder.parsed("talent", talent.getDisplayName()));

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

        Component lvlUpComponent = MiniMessage.miniMessage()
                .deserialize(config.getString("lang.messages.level-up"), TagResolver.resolver(resolvers));
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

    private static boolean crossedLevel(PlayerLevelUpEvent event, int requiredLevel) {
        return requiredLevel > event.getPreviousLevel() && requiredLevel <= event.getNewLevel();
    }

    private static void sendAbilityMessage(Player player, Component prefix, String message,
            List<TagResolver.Single> resolvers) {
        Component component = MiniMessage.miniMessage().deserialize(message, TagResolver.resolver(resolvers));
        player.sendMessage(prefix.append(component));
    }

}
