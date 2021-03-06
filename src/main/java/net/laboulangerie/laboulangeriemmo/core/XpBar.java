package net.laboulangerie.laboulangeriemmo.core;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;

public class XpBar {
    public static void displayBar(Talent talent, MmoPlayer mmoPlayer) {
        FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();
        if (!config.getBoolean("enable-xp-bar", false)) return;

        List<TagResolver.Single> placeholders = Arrays.asList(
            Placeholder.parsed("talent", talent.getDisplayName()),
            Placeholder.parsed("level", Integer.toString(talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER))),
            Placeholder.parsed("xp", Double.toString(talent.getXp() - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER))),
            Placeholder.parsed("max_xp", Double.toString(talent.getXpToNextLevel(LaBoulangerieMmo.XP_MULTIPLIER)))
        );
        
        final BossBar bossbar = BossBar.bossBar(
                MiniMessage.miniMessage().deserialize(config.getString("lang.bar.format"), TagResolver.resolver(placeholders)),
                (float) (((talent.getXp() - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER))
                        / talent.getXpToNextLevel(LaBoulangerieMmo.XP_MULTIPLIER))),
                BossBar.Color.valueOf(config.getString("lang.bar.color")),
                BossBar.Overlay.valueOf(config.getString("lang.bar.style"))
        );

        Player player = Bukkit.getPlayer(mmoPlayer.getUniqueId());
        player.showBossBar(bossbar);

        Bukkit.getScheduler().runTaskLater(LaBoulangerieMmo.PLUGIN, () -> {
            player.hideBossBar(bossbar);
        }, config.getLong("lang.bar.delay"));
    }
}