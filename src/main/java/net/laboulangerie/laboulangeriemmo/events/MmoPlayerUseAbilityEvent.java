package net.laboulangerie.laboulangeriemmo.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.player.ability.Abilities;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class MmoPlayerUseAbilityEvent extends Event{

    private static HandlerList HANDLERS = new HandlerList();
    private MmoPlayer mmoPlayer;
    private Abilities ability;
    private AbilityTrigger trigger;

    public MmoPlayerUseAbilityEvent(MmoPlayer mmoPlayer, Abilities ability, AbilityTrigger trigger) {
        this.mmoPlayer = mmoPlayer;
        this.ability = ability;
        this.trigger = trigger;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public MmoPlayer getMmoPlayer() {
        return mmoPlayer;
    }
    public AbilityTrigger getAbilityTrigger() {
        return trigger;
    }

    public Abilities getAbility() {
        return ability;
    }

    public String getAbilityEventName() {
        if (trigger == AbilityTrigger.BREAK) {
            return "BlockBreakEvent";
        }
        if (trigger == AbilityTrigger.BREED) {
            return "EntityBreedEvent";
        }
        if (trigger == AbilityTrigger.COMBO) {
            return "ComboCompletedEvent";
        }
        if (trigger == AbilityTrigger.EAT) {
            return "PlayerConsumeItemEvent";
        }
        if (trigger == AbilityTrigger.HOLD_ITEM) {
            return "PlayerItemHeldEvent";
        }
        if (trigger == AbilityTrigger.LEFT_CLICK_ENTITY) {
            return "EntityHurtEvent";
        }
        if (trigger == AbilityTrigger.RIGHT_CLICK_ENTITY) {
            return "PlayerInteractEntityEvent";
        }
        if (trigger == AbilityTrigger.RIGHT_CLICK_AIR || trigger == AbilityTrigger.RIGHT_CLICK_BLOCK || trigger == AbilityTrigger.LEFT_CLICK_AIR || trigger == AbilityTrigger.LEFT_CLICK_BLOCK) {
            return "PlayerInteractEvent";
        }
        return null;
    }

}
