package net.laboulangerie.laboulangeriemmo.api.rareloot;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class RegisterRareLootItemProvidersEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final RareLootItemProviderRegistry registry;

    public RegisterRareLootItemProvidersEvent(RareLootItemProviderRegistry registry) {
        this.registry = registry;
    }

    public RareLootItemProviderRegistry getRegistry() {
        return registry;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
