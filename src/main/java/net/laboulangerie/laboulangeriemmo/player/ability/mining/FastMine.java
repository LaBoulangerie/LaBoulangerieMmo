package net.laboulangerie.laboulangeriemmo.player.ability.mining;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class FastMine extends AbilityExecutor {

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.LEFT_CLICK_BLOCK;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        ItemStack item = event.getItem();
        return item != null
                && (item.getType() == Material.NETHERITE_PICKAXE || item.getType() == Material.DIAMOND_PICKAXE
                        || item.getType() == Material.IRON_PICKAXE)
                && Tag.BASE_STONE_OVERWORLD.isTagged(event.getClickedBlock().getType());
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
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