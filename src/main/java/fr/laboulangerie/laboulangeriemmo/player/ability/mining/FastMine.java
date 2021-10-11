package fr.laboulangerie.laboulangeriemmo.player.ability.mining;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class FastMine extends AbilityExecutor {

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.LEFT_CLICK_BLOCK;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        ItemStack item = event.getItem();
        return item != null && item.getType() == Material.NETHERITE_PICKAXE;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Player player = event.getPlayer();
        int duration = 0;
        int amplifier = 0;
        if (level >= 60) {
            duration = 300;
            amplifier = 3;
        }else if (level >= 40) {
            duration = 60;
            amplifier = 2;
        }else {
            duration = 20;
            amplifier = 1;
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration*20, amplifier, true));
    }
}
