package net.laboulangerie.laboulangeriemmo.core.hiding;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class InvisiblePlayer {

    public static final Set<InvisiblePlayer> invisiblePlayer = new HashSet<>();

    private final Player player;

    private final int abilityLevel;

    public boolean abilityCancelled;

    public InvisiblePlayer(Player player, int abilityLevel) {
        this.player = player;
        this.abilityLevel = abilityLevel;
        this.abilityCancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public int getAbilityLevel() {
        return abilityLevel;
    }

    public static InvisiblePlayer getInvisiblePlayer(Player player) {
        for (InvisiblePlayer p : invisiblePlayer) {
            if (p.getPlayer().getUniqueId().equals(player.getUniqueId()))
                return (p);
        }
        return (null);
    }
}
