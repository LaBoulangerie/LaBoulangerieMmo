package net.laboulangerie.laboulangeriemmo.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;

public class ComboCompletedEvent extends Event {
    private static HandlerList HANDLERS = new HandlerList();
    private Player player;
    private KeyStreak streak;

    public ComboCompletedEvent(Player player, KeyStreak streak) {
        this.player = player;
        this.streak = streak;
    }

    public Player getPlayer() {
        return player;
    }

    public KeyStreak getKeyStreak() {
        return streak;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
