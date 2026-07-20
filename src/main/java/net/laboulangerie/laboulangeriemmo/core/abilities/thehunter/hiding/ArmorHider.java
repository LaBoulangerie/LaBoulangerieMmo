package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.hiding;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ArmorHider {

    public static void hideArmor(Player viewer, Player target) {
        ItemStack air = new ItemStack(Material.AIR);
        viewer.sendEquipmentChange(target, EquipmentSlot.HEAD, air);
        viewer.sendEquipmentChange(target, EquipmentSlot.CHEST, air);
        viewer.sendEquipmentChange(target, EquipmentSlot.LEGS, air);
        viewer.sendEquipmentChange(target, EquipmentSlot.FEET, air);
    }

    public static void restoreArmor(Player viewer, Player target) {
        viewer.sendEquipmentChange(target, EquipmentSlot.HEAD,
            target.getInventory().getHelmet());
        viewer.sendEquipmentChange(target, EquipmentSlot.CHEST,
            target.getInventory().getChestplate());
        viewer.sendEquipmentChange(target, EquipmentSlot.LEGS,
            target.getInventory().getLeggings());
        viewer.sendEquipmentChange(target, EquipmentSlot.FEET,
            target.getInventory().getBoots());
    }
}
