package net.laboulangerie.laboulangeriemmo.abilities.mining;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.laboulangerie.laboulangeriemmo.abilities.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.abilities.AbilityTrigger;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;

public class FastMine extends AbilityExecutor {

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.COMBO;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        return item != null
                && (item.getType() == Material.NETHERITE_PICKAXE || item.getType() == Material.DIAMOND_PICKAXE
                        || item.getType() == Material.IRON_PICKAXE)
                && event.getKeyStreak().match(new KeyStreak(ComboKey.LEFT, ComboKey.LEFT, ComboKey.LEFT));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        Player player = event.getPlayer();
        int duration = 0;
        int amplifier = 0;
        if (level >= 75) {
            duration = 300;
            amplifier = 3;
        } else if (level >= 45) {
            duration = 60;
            amplifier = 2;
        } else {
            duration = 20;
            amplifier = 1;
        }
        player.sendMessage("§eVous gagnez Haste " + (amplifier + 1) + " pendant " + duration + " secondes");
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration * 20, amplifier, true));
        EffectRegistry.playEffect("default", player);
    }
}
