package net.laboulangerie.laboulangeriemmo.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;

public class PlayerEarnsXpEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private double amount;
    private String talentName;
    private MmoPlayer player;

    public PlayerEarnsXpEvent(double amount, String talentName, MmoPlayer player) {
        this.amount = amount;
        this.talentName = talentName;
        this.player = player;
    }

    public double getAmount() {
        return amount;
    }

    public String getTalentName() {
        return talentName;
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

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
