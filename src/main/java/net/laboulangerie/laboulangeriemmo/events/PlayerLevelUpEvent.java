package net.laboulangerie.laboulangeriemmo.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;

public class PlayerLevelUpEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Talent talent;
    private MmoPlayer player;

    public PlayerLevelUpEvent(Talent talent, MmoPlayer player) {
        this.talent = talent;
        this.player = player;
    }

    public Talent getTalent() {
        return talent;
    }

    public MmoPlayer getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
