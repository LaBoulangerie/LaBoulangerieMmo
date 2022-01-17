package fr.laboulangerie.laboulangeriemmo.core;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Bar {
    private FileConfiguration config;
	private BossBar bossbar;
	
	public Bar () {
        this.config = LaBoulangerieMmo.PLUGIN.getConfig();
	}

	public void displayBar(Talent talent, MmoPlayer mmoPlayer) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("talent", talent.getDisplayName());
        placeholders.put("level", Integer.toString(talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER)));
        placeholders.put("xp", Double.toString(talent.getXp() - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER)));
        placeholders.put("max_xp", Integer.toString(talent.getXpToNextLevel(LaBoulangerieMmo.XP_MULTIPLIER)));

		bossbar = BossBar.bossBar(
            MiniMessage.get().parse(config.getString("lang.bar.format"), placeholders),
            (float) ((talent.getXp() - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER) / talent.getXpToNextLevel(LaBoulangerieMmo.XP_MULTIPLIER))/100),
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