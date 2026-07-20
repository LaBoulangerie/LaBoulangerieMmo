package net.laboulangerie.laboulangeriemmo.core.abilities.mining;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;

public class LuckyVein extends AbilityExecutor {

    public static final Map<UUID, FortuneBonus> activeBuffs = new HashMap<>();

    public static class FortuneBonus {
        public final float bonus;
        public final long expireTime;

        public FortuneBonus(float bonus, long expireTime) {
            this.bonus = bonus;
            this.expireTime = expireTime;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    public LuckyVein(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        return event.getKeyStreak().match(new KeyStreak(ComboKey.RIGHT, ComboKey.LEFT, ComboKey.RIGHT));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        Player player = event.getPlayer();

        float fortuneBonus;
        int durationSeconds;

        if (level >= getTier(2)) {
            fortuneBonus = 1.0f;
            durationSeconds = 45;
        } else if (level >= getTier(1)) {
            fortuneBonus = 0.75f;
            durationSeconds = 30;
        } else {
            fortuneBonus = 0.5f;
            durationSeconds = 15;
        }

        long expireTime = System.currentTimeMillis() + (durationSeconds * 1000L);
        activeBuffs.put(player.getUniqueId(), new FortuneBonus(fortuneBonus, expireTime));

        player.sendMessage("§eFilon Chanceux activé ! Bonus Fortune +" + fortuneBonus + " pendant " + durationSeconds + "s");
        EffectRegistry.playEffect("default", player);
    }

    public static FortuneBonus getActiveBonus(UUID uuid) {
        FortuneBonus bonus = activeBuffs.get(uuid);
        if (bonus != null && bonus.isExpired()) {
            activeBuffs.remove(uuid);
            return null;
        }
        return bonus;
    }
}
