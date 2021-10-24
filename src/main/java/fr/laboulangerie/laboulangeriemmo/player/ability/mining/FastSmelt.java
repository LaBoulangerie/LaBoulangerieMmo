package fr.laboulangerie.laboulangeriemmo.player.ability.mining;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;
import fr.laboulangerie.laboulangeriemmo.player.ability.ParticulEffect;

public class FastSmelt extends AbilityExecutor {

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.LEFT_CLICK_BLOCK;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        if (event.getClickedBlock().getType() != Material.FURNACE || event.getItem().getType() != Material.COAL) return false;

        Furnace furnace = (Furnace) event.getClickedBlock().getState();
        ItemStack toSmelt = furnace.getInventory().getSmelting();
        if (toSmelt == null || toSmelt.getType() == Material.AIR) return false;

        ItemStack result = null;
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()) {
            Recipe recipe = iter.next();

            if (!(recipe instanceof FurnaceRecipe)) continue;
            if (((FurnaceRecipe) recipe).getInput().getType() != toSmelt.getType()) continue;

            result = recipe.getResult();
            break;
        }
        return result != null;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Furnace furnace = (Furnace) event.getClickedBlock().getState();
        Player bukkitPlayer = event.getPlayer();

        ItemStack result = null;
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()) {
            Recipe recipe = iter.next();

            if (!(recipe instanceof FurnaceRecipe)) continue;
            if (((FurnaceRecipe) recipe).getInput().getType() != furnace.getInventory().getSmelting().getType())
                continue;

            result = recipe.getResult();
            break;
        }
        FurnaceInventory inv = furnace.getInventory();
        result.setAmount(inv.getSmelting().getAmount());
        inv.setSmelting(new ItemStack(Material.AIR));

        if (inv.getResult() == null || (inv.getResult().getType() == result.getType() && inv.getResult().getAmount() + result.getAmount() <= 64)) {
            result.setAmount(result.getAmount() + (inv.getResult() != null ? inv.getResult().getAmount() : 0));
            inv.setResult(result);
        }else {
            bukkitPlayer.getWorld().dropItemNaturally(bukkitPlayer.getLocation(), result);
        }

        ItemStack newHand = bukkitPlayer.getInventory().getItemInMainHand();

        if (bukkitPlayer.getInventory().getItemInMainHand().getAmount() > 1)
            newHand.setAmount(newHand.getAmount() - 1);
        else
            newHand = new ItemStack(Material.AIR);

        bukkitPlayer.getInventory().setItemInMainHand(newHand);
        new ParticulEffect().createHelix(bukkitPlayer);
        bukkitPlayer.sendMessage(ChatColor.GOLD + "[LaBoulangerieMmo] " + ChatColor.YELLOW + "Le contenu du four a été cuit.");
        
    }
}
