package net.laboulangerie.laboulangeriemmo.utils;

import com.google.common.collect.Multimap;

import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.core.IRegistry;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class Utils {
    public static double getAttackDamage(Player player, ItemStack itemStack) {
       
        net.minecraft.world.item.ItemStack craftItemStack = CraftItemStack.asNMSCopy(player.getInventory().getItem(player.getInventory().getHeldItemSlot()));
        net.minecraft.world.item.Item item = craftItemStack.getItem();
        
        double damages = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() - 7;

        if(item instanceof net.minecraft.world.item.ItemSword || item instanceof net.minecraft.world.item.ItemTool || item instanceof net.minecraft.world.item.ItemHoe || item instanceof net.minecraft.world.item.ItemTrident) {

            Multimap<AttributeBase, AttributeModifier> attributes = craftItemStack.getItem().a(EnumItemSlot.a);
            AttributeBase base = IRegistry.al.get(CraftNamespacedKey.toMinecraft(Attribute.GENERIC_ATTACK_DAMAGE.getKey()));
            damages += ((AttributeModifier) attributes.get(base).toArray()[0]).getAmount() + 1;

            if (itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) != 0) {
                damages += 0.5 * itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) + 0.5;
            }
        }

        return damages;
    }
}        
