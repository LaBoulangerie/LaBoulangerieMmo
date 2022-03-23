package net.laboulangerie.laboulangeriemmo.core.hiding;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArmorHider {

    // DO NOT RENAME THEY MATCH MOJANG NAMES
    public enum EquipmentSlot {
        MAINHAND,
        OFFHAND,
        FEET,
        LEGS,
        CHEST,
        HEAD
    }

    private static Class<?> getClass(String packageName, String name) {
        try {
            return Class.forName(packageName + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void hideArmor(Player other, Player self) {
        sendPacket(other, self.getEntityId(), 0, EquipmentSlot.HEAD);
        sendPacket(other, self.getEntityId(), 0, EquipmentSlot.CHEST);
        sendPacket(other, self.getEntityId(), 0, EquipmentSlot.LEGS);
        sendPacket(other, self.getEntityId(), 0, EquipmentSlot.FEET);
    }

    private static void sendPacket(Player player, int entityID, int itemID, EquipmentSlot slot) {
        try {
            final Class<?> packetClass = getClass("net.minecraft.network.protocol.game", "PacketPlayOutEntityEquipment");
            final Class<?> itemClass = getClass("net.minecraft.world.item", "Item");
            final Class<?> itemStackClass = getClass("net.minecraft.world.item", "ItemStack");
            final Class<?> iMaterialClass = getClass("net.minecraft.world.level", "IMaterial");
            final Class<?> pairClass = getClass("com.mojang.datafixers.util", "Pair");
            final Class enumClass = getClass("net.minecraft.world.entity", "EnumItemSlot");

            final Enum enumSlot = Enum.valueOf(enumClass, slot.toString());

            final Constructor<?> pairConstructor = pairClass.getConstructor(Object.class, Object.class);
            final Constructor<?> packetConstructor = packetClass.getConstructor(int.class, List.class);
            final Constructor<?> itemStackConstructor = itemStackClass.getConstructor(iMaterialClass);

            final Object itemStack = itemStackConstructor.newInstance(itemClass.getMethod("b", int.class).invoke(null, itemID));
            final Object pair = pairConstructor.newInstance(enumSlot, itemStack);

            final List<Object> equipmentSlots = new ArrayList<>();
            equipmentSlots.add(pair);

            final Object packet = packetConstructor.newInstance(entityID, equipmentSlots);
            final Object handle = player.getClass().getMethod("getHandle").invoke(player);
            final Object playerConnection = handle.getClass().getField("b").get(handle);
            final Object networkManager = playerConnection.getClass().getField("a").get(playerConnection);

            networkManager.getClass().getMethod("a", getClass("net.minecraft.network.protocol", "Packet")).invoke(networkManager, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reloadArmor(Player player, Player invisiblePlayer, EquipmentSlot slot) {
        try {
            final Class<?> packetClass = getClass("net.minecraft.network.protocol.game", "PacketPlayOutEntityEquipment");
            final Class<?> itemStackClass = getClass("net.minecraft.world.item", "ItemStack");
            final Class<?> iMaterialClass = getClass("net.minecraft.world.level", "IMaterial");
            final Class<?> pairClass = getClass("com.mojang.datafixers.util", "Pair");
            final Class enumClass = getClass("net.minecraft.world.entity", "EnumItemSlot");

            final Enum enumSlot = Enum.valueOf(enumClass, slot.toString());

            final Constructor<?> pairConstructor = pairClass.getConstructor(Object.class, Object.class);
            final Constructor<?> packetConstructor = packetClass.getConstructor(int.class, List.class);
            final Constructor<?> itemStackConstructor = itemStackClass.getConstructor(iMaterialClass);

            final Object playerObject = invisiblePlayer.getClass().getMethod("getHandle").invoke(invisiblePlayer);
            final Object inventory = playerObject.getClass().getMethod("fr").invoke(playerObject);
            final Object armorPiece = inventory.getClass().getMethod("e", int.class).invoke(inventory, slot.ordinal() - 2);
            final Object itemStack = itemStackConstructor.newInstance(armorPiece.getClass().getMethod("c").invoke(armorPiece));
            final Object pair = pairConstructor.newInstance(enumSlot, itemStack);

            final List<Object> equipmentSlots = new ArrayList<>();
            equipmentSlots.add(pair);

            final Object packet = packetConstructor.newInstance(invisiblePlayer.getEntityId(), equipmentSlots);
            final Object handle = player.getClass().getMethod("getHandle").invoke(player);
            final Object playerConnection = handle.getClass().getField("b").get(handle);
            final Object networkManager = playerConnection.getClass().getField("a").get(playerConnection);

            networkManager.getClass().getMethod("a", getClass("net.minecraft.network.protocol", "Packet")).invoke(networkManager, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
