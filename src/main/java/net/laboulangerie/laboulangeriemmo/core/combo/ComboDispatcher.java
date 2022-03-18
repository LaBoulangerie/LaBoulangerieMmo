package net.laboulangerie.laboulangeriemmo.core.combo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.Component;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;
import net.minecraft.world.inventory.ClickType;

public class ComboDispatcher implements Listener {
    private Map<Player, KeyStreak> comboStreaks = new HashMap<Player, KeyStreak>();
    /**
     * Used to skip combo keys, when other items are in hand
     */
    private List<String> authorizedTools = Arrays.asList("PICKAXE", "SWORD", "HOE", "AXE");
    /**
     * Used for protocol dark wizardry
     */
    private HashMap<UUID, Boolean> shouldCancelNextArmAnimation = new HashMap<UUID, Boolean>();

    public ComboDispatcher() {
        /*
         * Minecraft's protocol is fucked up, the only way to detect a left click in the
         * air is to listen for the arm animation packet
         * but it's also sent when the player drop or start breaking a block, so we
         * ignore the arm animation following a drop
         */
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(LaBoulangerieMmo.PLUGIN, ListenerPriority.MONITOR, PacketType.Play.Client.BLOCK_DIG) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        PlayerDigType digType = event.getPacket().getPlayerDigTypes().getValues().get(0);

                        if (digType == PlayerDigType.DROP_ALL_ITEMS || digType == PlayerDigType.DROP_ITEM
                                || digType == PlayerDigType.START_DESTROY_BLOCK) {
                            shouldCancelNextArmAnimation.put(event.getPlayer().getUniqueId(), true);
                        }
                    }
                });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(LaBoulangerieMmo.PLUGIN,
                ListenerPriority.MONITOR, PacketType.Play.Client.WINDOW_CLICK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                /*
                 * See https://wiki.vg/Protocol#Click_Window if "mode" is throw
                 * an item or a full stack of item have been dropped from the inventory window.
                 * UPDATE SENSITIVE, NMS
                 */

                Integer button = packet.getIntegers().read(2); // the button field as defined in the protocol specification, -999 = outside the window

                /*We have to handle a special case for some obscures reasons whe you click outside the window with an empty cursor,
                  the action is labeled as drop but nothing is dropped but when you right or left click outside a window and you
                  actually drop an item, the action is labeled as pickup*/
                if ((packet.getEnumModifier(ClickType.class, ClickType.class).getValues().get(0) == ClickType.THROW && button != -999)
                        || (button == -999 && packet.getEnumModifier(ClickType.class, ClickType.class).getValues().get(0) == ClickType.PICKUP)) {
                    shouldCancelNextArmAnimation.put(event.getPlayer().getUniqueId(), true);
                }
            }
        });
    }

    @EventHandler
    public void onComboKeyPress(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        String[] bits = event.getItem().getType().toString().split("_");
        if (!authorizedTools.contains(bits[bits.length - 1]))
            return;

        ComboKey key = null;
        Boolean shouldCancel = shouldCancelNextArmAnimation.get(event.getPlayer().getUniqueId());
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
                if (shouldCancel != null && shouldCancel) {
                    shouldCancelNextArmAnimation.put(event.getPlayer().getUniqueId(), false);
                    return;
                }
            case LEFT_CLICK_BLOCK:
                key = ComboKey.LEFT;
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                key = ComboKey.RIGHT;
                break;
            default:
                return;
        }
        handleKey(event.getPlayer(), key);
    }

    private void handleKey(Player player, ComboKey key) {
        if (!LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player).hasEnabledCombo())
            return;

        KeyStreak streak = comboStreaks.get(player);
        if (streak == null) {
            streak = new KeyStreak();
            comboStreaks.put(player, streak);
        }

        BukkitRunnable timer = new BukkitRunnable() {
            @Override
            public void run() {
                comboStreaks.get(player).fail(); // Fail the streak if there is no new key during 10 ticks
            }
        };
        timer.runTaskLaterAsynchronously(LaBoulangerieMmo.PLUGIN, 10);
        boolean isFull = streak.addKey(key, timer); // A streak is full when there is 3 keys in it

        player.sendActionBar(Component.text(
                "§6" + streak.getKeyQueue().stream().map(Object::toString)
                        .collect(Collectors.joining(" §r-§6 "))));

        if (isFull) {
            Bukkit.getPluginManager().callEvent(new ComboCompletedEvent(player, streak));
            streak.fail(); // the combo didn't fail but we use that to empty it
        }
    }
}
