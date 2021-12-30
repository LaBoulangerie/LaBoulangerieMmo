package fr.laboulangerie.laboulangeriemmo.player.ability.thehunter;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;
import net.kyori.adventure.text.Component;

public class ExpInBottle extends AbilityExecutor {

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.RIGHT_CLICK_AIR;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        ItemStack item = event.getItem();
        return event.getPlayer().isSneaking() && item != null && item.getType() == Material.GLASS_BOTTLE;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Player player = event.getPlayer();
        int lvlToSubtract = 10; //tier 1
        int playerCurrentLevel = player.getTotalExperience();

        if (level >= 70) lvlToSubtract = 30; //tier 3
        else if (level >= 50) lvlToSubtract = 20; //tier 2
        
        if (playerCurrentLevel >= lvlToSubtract) {
            player.setLevel(0); // this is a needed trick, it works, don't touch it :p
            player.setExp(0);
            player.setTotalExperience(0);
            player.giveExp(playerCurrentLevel - lvlToSubtract);

            ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.lore(Arrays.asList(Component.text("Quantité: "+lvlToSubtract+" xp")));
            item.setItemMeta(itemMeta);
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-1);

            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
            }else {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }
}
