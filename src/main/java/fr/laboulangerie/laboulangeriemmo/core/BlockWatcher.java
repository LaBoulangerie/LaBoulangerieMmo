package fr.laboulangerie.laboulangeriemmo.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class BlockWatcher {
    private int entityId;
    private List<Player> watchers = new ArrayList<Player>();

    BlockWatcher(int entityId, Player firstWatcher) {
        this.entityId = entityId;
        watchers.add(firstWatcher);
    }

    public int getEntityId() {
        return entityId;
    }

    public List<Player> getWatchers() {
        return watchers;
    }

    public boolean isWatching(Player player) {
        return watchers.contains(player);
    }

    public void addWatcher(Player player) {
        watchers.add(player);
    }

    /**
     * @return true if there's no other watchers
     */
    public boolean removeWatcher(Player player) {
        watchers.remove(player);
        return watchers.size() == 0;
    }
}
