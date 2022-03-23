package net.laboulangerie.laboulangeriemmo.player.ability.thehunter;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class FireBow extends AbilityExecutor {
    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.COMBO;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;

        boolean hasFlameBow = event.getPlayer().getInventory().contains(Material.BOW)
            && event.getPlayer().getInventory().all(Material.BOW).values().stream()
                .map(item -> item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.ARROW_FIRE))
                .reduce(false, (result, hasEnchant)-> (result == true) || hasEnchant);

        return new KeyStreak(ComboKey.RIGHT, ComboKey.LEFT, ComboKey.LEFT).match(event.getKeyStreak())
            && hasFlameBow;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        Player player = event.getPlayer();
    }
}
