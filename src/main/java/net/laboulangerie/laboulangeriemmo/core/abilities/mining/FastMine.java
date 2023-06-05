package net.laboulangerie.laboulangeriemmo.core.abilities.mining;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;

public class FastMine extends AbilityExecutor {

    public FastMine(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        return event.getKeyStreak()
                .match(new KeyStreak(ComboKey.LEFT, ComboKey.RIGHT, ComboKey.LEFT));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        Player player = event.getPlayer();
        int duration = 0;
        int amplifier = 0;
        if (level >= getTier(2)) {
            duration = 300;
            amplifier = 3;
        } else if (level >= getTier(1)) {
            duration = 120;
            amplifier = 2;
        } else {
            duration = 60;
            amplifier = 1;
        }
        player.sendMessage(
                "§eVous gagnez Haste " + (amplifier + 1) + " pendant " + duration + " secondes");
        player.addPotionEffect(
                new PotionEffect(PotionEffectType.FAST_DIGGING, duration * 20, amplifier, true));
        EffectRegistry.playEffect("default", player);
    }
}
