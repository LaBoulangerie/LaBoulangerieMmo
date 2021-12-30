package fr.laboulangerie.laboulangeriemmo.core;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.listener.MmoListener;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class Bar {
	
	private final LaBoulangerieMmo plugin;
	public BossBar bar2;
	private MmoListener mmoListener;
	public Bar bar;
	
	public Bar (LaBoulangerieMmo plugin) {
		this.plugin = plugin;
		this.mmoListener = new MmoListener(this);
	}
	
	public void createbar(Talent talent, MmoPlayer mmoPlayer) {
		
		
		bar2 = Bukkit.createBossBar(talent.getDisplayName() + " : " + talent.getLevel(0.2) + " | " + (talent.getXp() - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER)) + "/" + ((int) Math.pow((talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) + 1) / LaBoulangerieMmo.XP_MULTIPLIER, 2) - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER)), BarColor.GREEN, BarStyle.SOLID);
		bar2.addPlayer(Bukkit.getPlayer(mmoPlayer.getUniqueId()));
		Double progress = (talent.getXp() - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER))/((int) Math.pow((talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER) + 1) / LaBoulangerieMmo.XP_MULTIPLIER, 2) - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER));
		bar2.setProgress(progress);
		bar2.setVisible(true);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			bar2.setVisible(false);
		}, 60L);
	}
	
	public BossBar getBar() {
		return bar2;
	}
	
}
