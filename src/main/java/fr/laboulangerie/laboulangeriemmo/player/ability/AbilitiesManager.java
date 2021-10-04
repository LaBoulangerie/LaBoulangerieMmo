package fr.laboulangerie.laboulangeriemmo.player.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayer;

public class AbilitiesManager implements Listener {

    private LaBoulangerieMmo plugin;

    public AbilitiesManager(LaBoulangerieMmo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        MmoPlayer player = plugin.getMmoPlayerManager().getPlayer(event.getPlayer());

        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
                Abilities.supplier().get()
                    .filter(x ->
                        x.getExecutor().getAbilityTrigger() == AbilityTrigger.LEFT_CLICK_AIR
                        && player.canUseAbility(x)
                        && x.getExecutor().shouldTrigger(event)
                    )
                    .forEach(x -> {
                        x.getExecutor().trigger(event, player.getTalent(x.getParentTalent()).getLevel(0.2));
                        player.useAbility(x);
                    });
                break;
            case RIGHT_CLICK_AIR:
                Abilities.supplier().get()
                    .filter(x ->
                        x.getExecutor().getAbilityTrigger() == AbilityTrigger.RIGHT_CLICK_AIR
                        && player.canUseAbility(x)
                        && x.getExecutor().shouldTrigger(event)
                    )
                    .forEach(x -> {
                        x.getExecutor().trigger(event, player.getTalent(x.getParentTalent()).getLevel(0.2));
                        player.useAbility(x);
                    });
                break;
            case LEFT_CLICK_BLOCK:
                Abilities.supplier().get()
                    .filter(x ->
                        x.getExecutor().getAbilityTrigger() == AbilityTrigger.LEFT_CLICK_BLOCK
                        && player.canUseAbility(x)
                        && x.getExecutor().shouldTrigger(event)
                    )
                    .forEach(x -> {
                        x.getExecutor().trigger(event, player.getTalent(x.getParentTalent()).getLevel(0.2));
                        player.useAbility(x);
                    });
                break;
            case RIGHT_CLICK_BLOCK:
                Abilities.supplier().get()
                    .filter(x ->
                        x.getExecutor().getAbilityTrigger() == AbilityTrigger.RIGHT_CLICK_BLOCK
                        && player.canUseAbility(x)
                        && x.getExecutor().shouldTrigger(event)
                    )
                    .forEach(x -> {
                        x.getExecutor().trigger(event, player.getTalent(x.getParentTalent()).getLevel(0.2));
                        player.useAbility(x);
                    });
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onConsumeItem(PlayerItemConsumeEvent event) {
        MmoPlayer player = plugin.getMmoPlayerManager().getPlayer(event.getPlayer());
        Abilities.supplier().get()
            .filter(x ->
                x.getExecutor().getAbilityTrigger() == AbilityTrigger.EAT
                && player.canUseAbility(x)
                && x.getExecutor().shouldTrigger(event)
            )
            .forEach(x -> {
                x.getExecutor().trigger(event, player.getTalent(x.getParentTalent()).getLevel(0.2));
                player.useAbility(x);
            });
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        MmoPlayer player = plugin.getMmoPlayerManager().getPlayer(event.getPlayer());
        Abilities.supplier().get()
            .filter(x ->
                x.getExecutor().getAbilityTrigger() == AbilityTrigger.PLACE
                && player.canUseAbility(x)
                && x.getExecutor().shouldTrigger(event)
            )
            .forEach(x -> {
                x.getExecutor().trigger(event, player.getTalent(x.getParentTalent()).getLevel(0.2));
                player.useAbility(x);
            });
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        MmoPlayer player = plugin.getMmoPlayerManager().getPlayer(event.getPlayer());
        Abilities.supplier().get()
            .filter(x ->
                x.getExecutor().getAbilityTrigger() == AbilityTrigger.BREAK
                && player.canUseAbility(x)
                && x.getExecutor().shouldTrigger(event)
            )
            .forEach(x -> {
                x.getExecutor().trigger(event, player.getTalent(x.getParentTalent()).getLevel(0.2));
                player.useAbility(x);
            });
    }

    @EventHandler
    public void onClickEntity(PlayerInteractEntityEvent event) {
        MmoPlayer player = plugin.getMmoPlayerManager().getPlayer(event.getPlayer());
        Abilities.supplier().get()
            .filter(x ->
                x.getExecutor().getAbilityTrigger() == AbilityTrigger.RIGHT_CLICK_ENTITY
                && player.canUseAbility(x)
                && x.getExecutor().shouldTrigger(event)
            )
            .forEach(x -> {
                x.getExecutor().trigger(event, player.getTalent(x.getParentTalent()).getLevel(0.2));
                player.useAbility(x);
            });
    }

    @EventHandler
    public void onHurtEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {

            MmoPlayer player = plugin.getMmoPlayerManager().getPlayer((Player) event.getDamager());
            Abilities.supplier().get()
                .filter(x ->
                    x.getExecutor().getAbilityTrigger() == AbilityTrigger.LEFT_CLICK_ENTITY
                    && player.canUseAbility(x)
                    && x.getExecutor().shouldTrigger(event)
                )
                .forEach(x -> {
                    x.getExecutor().trigger(event, player.getTalent(x.getParentTalent()).getLevel(0.2));
                    player.useAbility(x);
                });
        }
    }
}
