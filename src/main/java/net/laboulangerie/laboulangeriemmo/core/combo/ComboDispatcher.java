package net.laboulangerie.laboulangeriemmo.core.combo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

public class ComboDispatcher implements Listener {
    private Map<Player, KeyStreak> comboStreaks = new HashMap<Player, KeyStreak>();
    /**
     * Used to skip combo keys, when other items are in hand
     */
    private List<String> authorizedTools = Arrays.asList("PICKAXE", "SWORD", "HOE", "AXE");

    @EventHandler
    public void onComboKeyPress(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        String[] bits = event.getItem().getType().toString().split("_");
        if (!authorizedTools.contains(bits[bits.length - 1])) return;

        ComboKey key = null;
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
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
        if (!LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player).hasEnabledCombo()) return;
        
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
        boolean isFull = streak.addKey(key, timer); //A streak is full when there is 3 keys in it

        player.sendActionBar(Component.text(
                "§6" + streak.getKeyQueue().stream().map(Object::toString)
                        .collect(Collectors.joining(" §r-§6 "))));

        if (isFull) {
            Bukkit.getPluginManager().callEvent(new ComboCompletedEvent(player, streak));
            streak.fail(); // the combo didn't fail but we use that to empty it
        }
    }
}
