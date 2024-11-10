package net.laboulangerie.laboulangeriemmo.utils;

import java.util.Collection;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import com.google.common.collect.Multimap;

public class Utils {
    public static double getAttackDamage(Player player, ItemStack itemStack) {
        double damages = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE).getValue();

        Multimap<Attribute, AttributeModifier> attributesMap = itemStack.getType().getDefaultAttributeModifiers(EquipmentSlot.HAND);
        Collection<AttributeModifier> modifiers = attributesMap.get(Attribute.GENERIC_ATTACK_DAMAGE);
        if (!modifiers.isEmpty()) {
            damages += ((AttributeModifier) modifiers.toArray()[0]).getAmount() + 0.7;
        }

        if (itemStack.getEnchantmentLevel(Enchantment.SHARPNESS) != 0) {
            damages += 0.5 * itemStack.getEnchantmentLevel(Enchantment.SHARPNESS) + 0.5;
        }

        return damages;
    }

    public static int getExpAtLevel(int level) {
        if (level <= 16) return (int) (Math.pow(level, 2) + 6 * level);
        else if (level <= 31) return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360.0);
        else
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220.0);
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
