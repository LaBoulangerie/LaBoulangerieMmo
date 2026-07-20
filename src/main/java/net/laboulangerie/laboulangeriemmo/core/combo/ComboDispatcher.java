package net.laboulangerie.laboulangeriemmo.core.combo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;

public class ComboDispatcher implements Listener {
    private Map<Player, KeyStreak> comboStreaks = new HashMap<Player, KeyStreak>();

    private Map<UUID, Long> lastDropTime = new HashMap<>();
    private Map<UUID, Long> lastInventoryClickTime = new HashMap<>();
    private Map<UUID, Long> lastBlockDamageTime = new HashMap<>();

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        lastDropTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (event.getClick().name().contains("DROP") || event.getSlot() == -999) {
                lastInventoryClickTime.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        lastBlockDamageTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onComboKeyPress(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        if (!isAuthorizeItem(event.getItem())) return;

        ComboKey key = null;
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
                long now = System.currentTimeMillis();
                UUID uuid = event.getPlayer().getUniqueId();
                Long dropTime = lastDropTime.get(uuid);
                Long invTime = lastInventoryClickTime.get(uuid);
                Long blockTime = lastBlockDamageTime.get(uuid);

                if ((dropTime != null && now - dropTime < 50) ||
                    (invTime != null && now - invTime < 50) ||
                    (blockTime != null && now - blockTime < 50)) {
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
        if (!LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player).hasEnabledCombo()) return;

        KeyStreak streak = comboStreaks.get(player);
        if (streak == null) {
            streak = new KeyStreak();
            comboStreaks.put(player, streak);
        }

        BukkitRunnable timer = new BukkitRunnable() {
            @Override
            public void run() {
                comboStreaks.get(player).clear(); // Fail the streak if there is no new key during 10
                                                  // ticks
            }
        };

        timer.runTaskLaterAsynchronously(LaBoulangerieMmo.PLUGIN, 20);
        boolean isFull = streak.addKey(key, timer);

        displayCombo(player, streak);

        if (isFull) {
            Bukkit.getPluginManager().callEvent(new ComboCompletedEvent(player, streak));
            streak.clear(); // the combo didn't fail but we use that to empty it
        }
    }

    private void displayCombo(Player player, KeyStreak streak) {
        ConfigurationSection comboSection = LaBoulangerieMmo.PLUGIN.getConfig().getConfigurationSection("lang.combo");
        String delimiter = comboSection.getString("delimiter");
        List<String> keyStrings = new ArrayList<String>(
                Collections.nCopies(LaBoulangerieMmo.COMBO_LENGTH, comboSection.getString("empty")));

        Iterator<ComboKey> it = streak.getKeyQueue().iterator();
        int i = 0;

        while (it.hasNext()) {
            String keyName = it.next().name();
            // Second arg is default if nothing is found in config.
            String keyString = comboSection.getString(keyName.toLowerCase(), keyName);
            keyStrings.set(i, keyString);
            i++;
        }

        String comboDisplay = String.join(delimiter, keyStrings);
        Component comboComponent = MiniMessage.miniMessage().deserialize(comboDisplay);
        player.sendActionBar(comboComponent);
    }

    private boolean isAuthorizeItem(ItemStack item) {
        AtomicBoolean isAuthorized = new AtomicBoolean(false);
        LaBoulangerieMmo.talentsRegistry.getTalents().values().stream().forEach(talent -> {
            if (talent.comboItems != null && talent.comboItems.contains(item.getType())) isAuthorized.set(true);
        });
        return isAuthorized.get();
    }
}
