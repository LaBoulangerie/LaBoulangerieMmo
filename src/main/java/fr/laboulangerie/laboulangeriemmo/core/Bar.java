package fr.laboulangerie.laboulangeriemmo.core;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class Bar {
	
	private final LaBoulangerieMmo plugin;
	public BossBar bossbar;
	public Bar bar;
	
	public Bar (LaBoulangerieMmo plugin) {
		this.plugin = plugin;
	}
	
	public void createbar(Talent talent, MmoPlayer mmoPlayer) {
		bossbar = Bukkit.createBossBar(
			"§b"+talent.getDisplayName()
			+ " §r: §e" + talent.getLevel(0.2)
			+ " §5| §e" + (talent.getXp() - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER))
			+ "§5/§e" + (
				(int) Math.pow((talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) + 1)
				/ LaBoulangerieMmo.XP_MULTIPLIER, 2) - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER)
			),
			BarColor.GREEN, BarStyle.SOLID
		);
		bossbar.addPlayer(Bukkit.getPlayer(mmoPlayer.getUniqueId()));
		Double progress = (talent.getXp() - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER))/((int) Math.pow((talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) + 1) / LaBoulangerieMmo.XP_MULTIPLIER, 2) - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER));
		bossbar.setProgress(progress);
		bossbar.setVisible(true);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			bossbar.setVisible(false);
		}, 200L);
	}
	
	public BossBar getBar() {
		return bossbar;
	}
	
}
