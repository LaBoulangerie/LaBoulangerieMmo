package net.laboulangerie.laboulangeriemmo.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;

public class PlayerLevelUpEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Talent talent;
    private final MmoPlayer player;
    private final int previousLevel;
    private final int newLevel;

    public PlayerLevelUpEvent(Talent talent, MmoPlayer player) {
        this(talent, player, Math.max(0, talent.getLevel() - 1), talent.getLevel());
    }

    public PlayerLevelUpEvent(Talent talent, MmoPlayer player, int previousLevel, int newLevel) {
        this.talent = talent;
        this.player = player;
        this.previousLevel = previousLevel;
        this.newLevel = newLevel;
    }

    public Talent getTalent() {
        return talent;
    }

    public MmoPlayer getPlayer() {
        return player;
    }

    public int getPreviousLevel() {
        return previousLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
