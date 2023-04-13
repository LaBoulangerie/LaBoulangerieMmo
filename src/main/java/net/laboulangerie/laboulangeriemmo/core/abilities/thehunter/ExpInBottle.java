package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.utils.Utils;

public class ExpInBottle extends AbilityExecutor {

    public ExpInBottle(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        ItemStack item = event.getItem();
        return event.getPlayer().isSneaking() && item != null
                && item.getType() == Material.GLASS_BOTTLE;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Player player = event.getPlayer();
        int pointsToSubtract = 100; // tier 1
        int currentExp = Utils.getPlayerExp(player);

        if (level >= getTier(2)) pointsToSubtract = 300; // tier 3
        else if (level >= getTier(1)) pointsToSubtract = 200; // tier 2

        if (currentExp >= pointsToSubtract) {
            Utils.changePlayerExp(player, -pointsToSubtract);

            ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.lore(Arrays.asList(Component.text("Quantité: " + pointsToSubtract + " xp")));
            item.setItemMeta(itemMeta);
            event.getItem().setAmount(event.getItem().getAmount() - 1);

            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }
}
