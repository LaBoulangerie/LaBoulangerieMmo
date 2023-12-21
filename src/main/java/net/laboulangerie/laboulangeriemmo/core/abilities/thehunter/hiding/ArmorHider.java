package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.hiding;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.entity.EquipmentSlot;

public class ArmorHider {

    public static void hideArmor(Player other, Player self) {
        try {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
            packet.getIntegers().write(0, self.getEntityId());
            StructureModifier<Object> mods = packet.getModifier();
    
            List<Pair<Object, Object>> list = new ArrayList<>();
            list.add(Pair.of(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
            list.add(Pair.of(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
            list.add(Pair.of(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
            list.add(Pair.of(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
            mods.write(1, list);
    
            ProtocolLibrary.getProtocolManager().sendServerPacket(other, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreArmor(Player player, Player invisiblePlayer) {
        try {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
            packet.getIntegers().write(0, invisiblePlayer.getEntityId());
            StructureModifier<Object> mods = packet.getModifier();

            List<Pair<Object, Object>> list = new ArrayList<>();
            list.add(Pair.of(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(invisiblePlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.HEAD))));
            list.add(Pair.of(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(invisiblePlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.CHEST))));
            list.add(Pair.of(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(invisiblePlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.LEGS))));
            list.add(Pair.of(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(invisiblePlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.FEET))));
            mods.write(1, list);

            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
