package net.laboulangerie.laboulangeriemmo.abilities.thehunter;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityTrigger;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.core.thehunter.hiding.ArmorHider;
import net.laboulangerie.laboulangeriemmo.core.thehunter.hiding.InvisiblePlayer;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Hiding extends AbilityExecutor {
    public Hiding(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.COMBO;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        // I'm currently using a random combo as I don't know what you guys want
        // Feel free to edit this and delete those comments afterward

        final ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        return event.getKeyStreak().match(new KeyStreak(ComboKey.LEFT, ComboKey.RIGHT, ComboKey.RIGHT));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        final ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        final Player player = event.getPlayer();

        // Same as the combo I used random level so edit and delete this comment
        if (level >= 70) {
            totalInvisibility(new InvisiblePlayer(player, 3), 12000);
        } else if (level >= 50) {
            totalInvisibility(new InvisiblePlayer(player, 2), 6000);
        } else if (level >= 10) {
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
    }

    private void basicInvisibility(InvisiblePlayer invisiblePlayer) {
        invisiblePlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2400, 0, false, false));
        InvisiblePlayer.invisiblePlayer.add(invisiblePlayer);
        endScheduler(invisiblePlayer, 2400);
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
