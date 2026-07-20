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

public class StoneSkin extends AbilityExecutor {

    public StoneSkin(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        return event.getKeyStreak().match(new KeyStreak(ComboKey.LEFT, ComboKey.LEFT, ComboKey.RIGHT));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        Player player = event.getPlayer();

        int durationTicks;
        int amplifier;

        if (level >= getTier(2)) {
            durationTicks = 45 * 20; // 45 seconds
            amplifier = 1; // Resistance II
        } else if (level >= getTier(1)) {
            durationTicks = 45 * 20; // 45 seconds
            amplifier = 0; // Resistance I
        } else {
            durationTicks = 20 * 20; // 20 seconds
            amplifier = 0; // Resistance I
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationTicks, amplifier, true, true));
        EffectRegistry.playEffect("default", player);
    }
}
