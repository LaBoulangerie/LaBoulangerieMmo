package net.laboulangerie.laboulangeriemmo.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

/** Limits stationary grinding while keeping the state transient and per player. */
public class XpMovementGuard {
    private static final int ALLOWED_STATIONARY_GAINS = 2;
    private final Map<UUID, MovementState> states = new HashMap<>();

    public boolean canGainXp(Player player) {
        Location location = player.getLocation();
        UUID playerId = player.getUniqueId();
        UUID worldId = location.getWorld().getUID();
        double minimumDistance = Math.max(0,
                LaBoulangerieMmo.PLUGIN.getConfig().getDouble("anti-afk-xp.minimum-distance", 1.0));

        MovementState state = states.get(playerId);
        if (state == null || !state.worldId.equals(worldId)
                || state.distanceSquared(location) >= minimumDistance * minimumDistance) {
            states.put(playerId, new MovementState(worldId, location.getX(), location.getY(), location.getZ(), 1));
            return true;
        }

        if (state.acceptedGains >= ALLOWED_STATIONARY_GAINS) return false;
        state.acceptedGains++;
        return true;
    }

    private static final class MovementState {
        private final UUID worldId;
        private final double x;
        private final double y;
        private final double z;
        private int acceptedGains;

        private MovementState(UUID worldId, double x, double y, double z, int acceptedGains) {
            this.worldId = worldId;
            this.x = x;
            this.y = y;
            this.z = z;
            this.acceptedGains = acceptedGains;
        }

        private double distanceSquared(Location location) {
            double deltaX = location.getX() - x;
            double deltaY = location.getY() - y;
            double deltaZ = location.getZ() - z;
            return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
        }
    }
}
