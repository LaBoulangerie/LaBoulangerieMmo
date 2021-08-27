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
        return item != null && item.getType() == Material.DIAMOND_PICKAXE;
    }

    @Override
    public void trigger(Event baseEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Player player = event.getPlayer();
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20*20, 1, true));
    }
}
