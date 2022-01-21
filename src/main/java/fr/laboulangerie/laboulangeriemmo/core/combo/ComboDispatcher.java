package fr.laboulangerie.laboulangeriemmo.core.combo;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;
import net.kyori.adventure.text.Component;

public class ComboDispatcher implements Listener {
    private Map<Player, KeyStreak> comboStreaks = new HashMap<Player, KeyStreak>();
    @EventHandler
    public void onComboKeyPress(PlayerInteractEvent event) {
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
        KeyStreak streak = comboStreaks.get(player);
        if (streak == null) {
            streak = new KeyStreak();
            comboStreaks.put(player, streak);
        }

        BukkitRunnable timer = new BukkitRunnable() {
            @Override
            public void run() {
                comboStreaks.get(player).fail(); //Fail the streak if there is no new key during 10 ticks
            }
        };
        timer.runTaskLaterAsynchronously(LaBoulangerieMmo.PLUGIN, 10);
        boolean isFull = streak.addKey(key, timer);
        
        player.sendActionBar(Component.text(
            "§6" + streak.getKeyQueue().stream().map(Object::toString)
                .collect(Collectors.joining(" §r-§6 ")
        )));
        
        if (isFull) {
            Bukkit.getPluginManager().callEvent(new ComboCompletedEvent(player, streak));
            streak.fail(); //the combo didn't fail but we use that to empty it
        }
    }
}
