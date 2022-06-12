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
import net.laboulangerie.laboulangeriemmo.abilities.farmer.AnimalTwins;
import net.laboulangerie.laboulangeriemmo.abilities.farmer.BetterBonemeal;
import net.laboulangerie.laboulangeriemmo.abilities.farmer.NatureTouch;
import net.laboulangerie.laboulangeriemmo.abilities.farmer.TastyBread;
import net.laboulangerie.laboulangeriemmo.abilities.mining.FastMine;
import net.laboulangerie.laboulangeriemmo.abilities.mining.FastSmelt;
import net.laboulangerie.laboulangeriemmo.abilities.mining.MagneticField;
import net.laboulangerie.laboulangeriemmo.abilities.mining.MinecraftExpMultiplier;
import net.laboulangerie.laboulangeriemmo.abilities.thehunter.ExpInBottle;
import net.laboulangerie.laboulangeriemmo.abilities.thehunter.FireBow;
import net.laboulangerie.laboulangeriemmo.abilities.thehunter.Hiding;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.BetterAppleDrop;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.DoubleDropLog;
import net.laboulangerie.laboulangeriemmo.abilities.woodcutting.Timber;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityTrigger;
import net.laboulangerie.laboulangeriemmo.api.ability.RegisterAbilitiesEvent;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;
import net.laboulangerie.laboulangeriemmo.core.Bar;
import net.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
import net.laboulangerie.laboulangeriemmo.events.XpCountDownFinishedEvent;

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

    @EventHandler
    public void onRegisterTime(RegisterAbilitiesEvent event) {
        event.getRegistry().registerAbility("fast-mine", FastMine.class, AbilityTrigger.COMBO);
        event.getRegistry().registerAbility("fast-smelt", FastSmelt.class, AbilityTrigger.LEFT_CLICK_BLOCK);
        event.getRegistry().registerAbility("minecraft-exp-multiplier", MinecraftExpMultiplier.class, AbilityTrigger.BREAK);
        event.getRegistry().registerAbility("magnetic-field", MagneticField.class, AbilityTrigger.RIGHT_CLICK_AIR);

        event.getRegistry().registerAbility("animal-twins", AnimalTwins.class, AbilityTrigger.BREED);
        event.getRegistry().registerAbility("better-bonemeal", BetterBonemeal.class, AbilityTrigger.RIGHT_CLICK_BLOCK);
        event.getRegistry().registerAbility("nature-touch", NatureTouch.class, AbilityTrigger.BREAK);
        event.getRegistry().registerAbility("tasty-bread", TastyBread.class, AbilityTrigger.HOLD_ITEM);

        event.getRegistry().registerAbility("dodging", FastMine.class, AbilityTrigger.COMBO);
        event.getRegistry().registerAbility("xp-in-bottle", ExpInBottle.class, AbilityTrigger.RIGHT_CLICK_AIR);
        event.getRegistry().registerAbility("fire-bow", FireBow.class, AbilityTrigger.COMBO);
        event.getRegistry().registerAbility("hiding", Hiding.class, AbilityTrigger.COMBO);

        event.getRegistry().registerAbility("better-apple-drop", BetterAppleDrop.class, AbilityTrigger.BREAK);
        event.getRegistry().registerAbility("double-drop-log", DoubleDropLog.class, AbilityTrigger.BREAK);
        event.getRegistry().registerAbility("strip", MinecraftExpMultiplier.class, AbilityTrigger.COMBO);
        event.getRegistry().registerAbility("timber", Timber.class, AbilityTrigger.BREAK);
    }
}
