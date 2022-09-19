package net.laboulangerie.laboulangeriemmo.utils;

import com.google.common.collect.Multimap;

import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class Utils {
    public static double getAttackDamage(Player player, ItemStack itemStack) {
       
        net.minecraft.world.item.ItemStack craftItemStack = CraftItemStack.asNMSCopy(player.getInventory().getItem(player.getInventory().getHeldItemSlot()));
        net.minecraft.world.item.Item item = craftItemStack.getItem();
        double damages = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE).getValue() - 7;
        if(item instanceof net.minecraft.world.item.SwordItem || item instanceof net.minecraft.world.item.TieredItem || item instanceof net.minecraft.world.item.TridentItem) {

            Multimap<Attribute, AttributeModifier> attributes = craftItemStack.getItem().getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
            Attribute base = DefaultedRegistry.ATTRIBUTE.get(CraftNamespacedKey.toMinecraft(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE.getKey()));
            damages += ((AttributeModifier) attributes.get(base).toArray()[0]).getAmount() + 1;

            if (itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) != 0) {
                damages += 0.5 * itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) + 0.5;
            }
        }

        return damages;
    }

    public static int getExpAtLevel(int level) {
        if(level <= 16)
            return (int) (Math.pow(level,2) + 6*level);
        else if(level <= 31)
            return (int) (2.5*Math.pow(level,2) - 40.5*level + 360.0);
        else
            return (int) (4.5*Math.pow(level,2) - 162.5*level + 2220.0);
    }

    public static int getPlayerExp(Player player) {
        int exp = 0;

        // Get the amount of XP in past levels
        exp += getExpAtLevel(player.getLevel());

        // Get amount of XP towards next level
        exp += Math.round(player.getExpToLevel() * player.getExp());

        return exp;
    }

    public static void changePlayerExp(Player player, int exp) {
        int currentExp = getPlayerExp(player);

        // Reset player's current exp to 0
        player.setExp(0);
        player.setLevel(0);
        
        // Give the player their exp back, with the difference
        int newExp = currentExp + exp;
        player.giveExp(newExp);
    }
}
