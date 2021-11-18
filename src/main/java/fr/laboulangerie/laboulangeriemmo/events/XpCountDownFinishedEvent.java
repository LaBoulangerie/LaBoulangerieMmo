package fr.laboulangerie.laboulangeriemmo.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import fr.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class XpCountDownFinishedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Talent talent;
    private MmoPlayer player;
    private double amount;

    public XpCountDownFinishedEvent(Talent talent, MmoPlayer player, double amount) {
        this.talent = talent;
        this.player = player;
        this.amount = amount;
    }

    public Talent getTalent() {
        return talent;
    }
    public MmoPlayer getPlayer() {
        return player;
    }
    public double getAmount() {
        return amount;
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
