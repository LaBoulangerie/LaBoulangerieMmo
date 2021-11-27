package fr.laboulangerie.laboulangeriemmo.events;


import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import fr.laboulangerie.laboulangeriemmo.player.MmoPlayer;

public class MmoPlayerBreakBlockEvent extends Event {

    private static HandlerList HANDLERS = new HandlerList();

    private Player player;
    private MmoPlayer mmoPlayer;
    private Block block;

    public MmoPlayerBreakBlockEvent(Player player, MmoPlayer mmoPlayer, Block block) {
        this.player = player;
        this.mmoPlayer = mmoPlayer;
        this.block = block;
    }

    public Player getPlayer() {
        return player;
    }

    public MmoPlayer getMmoPlayer() {
        return mmoPlayer;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
