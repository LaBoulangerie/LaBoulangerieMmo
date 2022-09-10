package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.hiding.ArmorHider;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.hiding.InvisiblePlayer;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;

public class Hiding extends AbilityExecutor {
    public Hiding(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        final ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        return event.getKeyStreak().match(new KeyStreak(ComboKey.LEFT, ComboKey.RIGHT, ComboKey.RIGHT));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        final ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        final Player player = event.getPlayer();

        if (level >= getTier(2)) {
            totalInvisibility(new InvisiblePlayer(player, 3), 9600);
        } else if (level >= getTier(1)) {
            totalInvisibility(new InvisiblePlayer(player, 2), 6000);
        } else {
            basicInvisibility(new InvisiblePlayer(player, 1));
        }
    }

    private void totalInvisibility(InvisiblePlayer invisiblePlayer, int duration) {
        invisiblePlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0, false, false));
        InvisiblePlayer.invisiblePlayer.add(invisiblePlayer);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(invisiblePlayer.getPlayer().getUniqueId())) continue;
            ArmorHider.hideArmor(p, invisiblePlayer.getPlayer());
        }
        endScheduler(invisiblePlayer, duration);
        if (invisiblePlayer.getAbilityLevel() < 3) EffectRegistry.playEffect("invisible-particles", invisiblePlayer.getPlayer());
    }

    private void basicInvisibility(InvisiblePlayer invisiblePlayer) {
        invisiblePlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 3600, 0, false, false));
        InvisiblePlayer.invisiblePlayer.add(invisiblePlayer);
        endScheduler(invisiblePlayer, 2400);
        EffectRegistry.playEffect("invisible-particles", invisiblePlayer.getPlayer());
    }

    private void endScheduler(InvisiblePlayer invisiblePlayer, int duration) {
        invisiblePlayer.setScheduler(new BukkitRunnable() {
            @Override
            public void run() {
                invisiblePlayer.cancelAbility();
            }
        }.runTaskLater(LaBoulangerieMmo.PLUGIN, duration));
    }

}
