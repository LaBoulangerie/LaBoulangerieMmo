package net.laboulangerie.laboulangeriemmo.player.ability.thehunter;

import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;

public class Hiding extends AbilityExecutor {
    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.COMBO;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        // I'm currently using a random combo as I don't know what you guys want
        // Feel free to edit this and delete those comments afterward

        final ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        return event.getKeyStreak().match(new KeyStreak(ComboKey.LEFT, ComboKey.RIGHT, ComboKey.RIGHT));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        final ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        final Player player = event.getPlayer();

        // Same as the combo I used random level so edit and delete this comment
        if (level >= 70)
            tier3(player);
        else if (level >= 50)
            tier2(player);
        else if (level >= 10)
            tier1(player);
    }

    private void tier3(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(player.getUniqueId())) continue;
            hideArmor(p, player);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 12000, 0, false, false));
    }

    private void tier2(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(player.getUniqueId())) continue;
            hideArmor(p, player);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 6000, 0, false, false));
    }

    private void tier1(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2400, 0, false, false));
    }

    private enum EquipmentSlot {
        MAIN_HAND,
        OFF_HAND,
        BOOTS,
        LEGGINGS,
        CHESTPLATE,
        HELMET
    }

    private Class<?> getClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void hideArmor(Player other, Player self) {
        sendPacket(other, self.getEntityId(), 0, EquipmentSlot.HELMET);
        sendPacket(other, self.getEntityId(), 0, EquipmentSlot.CHESTPLATE);
        sendPacket(other, self.getEntityId(), 0, EquipmentSlot.LEGGINGS);
        sendPacket(other, self.getEntityId(), 0, EquipmentSlot.BOOTS);
    }

    private void sendPacket(Player player, int entityID, int itemID, EquipmentSlot slot) {
        try {
            final Class<?> packetClass = getClass("PacketPlayOutEntityEquipment");
            final Class<?> itemClass = getClass("Item");
            final Class<?> itemStackClass = getClass("ItemStack");

            final Constructor<?> packetConstructor = packetClass.getConstructor(int.class, int.class, itemStackClass);
            final Constructor<?> itemStackConstructor = itemStackClass.getConstructor(itemClass);

            final Object itemStack = itemStackConstructor.newInstance(itemClass.getMethod("getById", int.class).invoke(null, itemID));
            final Object packet = packetConstructor.newInstance(entityID, slot.ordinal(), itemStack);

            final Object handle = player.getClass().getMethod("getHandle").invoke(player);
            final Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            playerConnection.getClass().getMethod("sendPacket", getClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
