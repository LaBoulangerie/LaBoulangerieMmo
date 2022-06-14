package net.laboulangerie.laboulangeriemmo.core.abilities.mining;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class BlockWatcher {
    private int entityId;
    private UUID uuid;
    private List<Player> watchers = new ArrayList<Player>();

    public BlockWatcher(int entityId, UUID uuid, Player firstWatcher) {
        this.entityId = entityId;
        this.uuid = uuid;
        watchers.add(firstWatcher);
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getUuid() {
        return uuid;
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
