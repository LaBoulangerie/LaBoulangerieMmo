package net.laboulangerie.laboulangeriemmo.core.hiding;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class InvisiblePlayer {

    public static final Set<InvisiblePlayer> invisiblePlayer = new HashSet<>();

    private Player player;

    private final int abilityLevel;
    private BukkitTask scheduler;

    public boolean abilityCancelled;

    public InvisiblePlayer(Player player, int abilityLevel) {
        this.player = player;
        this.abilityLevel = abilityLevel;
        this.abilityCancelled = false;
    }

    public void setScheduler(BukkitTask scheduler) {
        this.scheduler = scheduler;
    }

    public Player getPlayer() {
        return player;
    }

    public int getAbilityLevel() {
        return abilityLevel;
    }

    private void setPlayer(Player player) {
        this.player = player;
    }

    public static InvisiblePlayer getInvisiblePlayer(Player player) {
        for (InvisiblePlayer p : invisiblePlayer) {
            if (p.getPlayer().getUniqueId().equals(player.getUniqueId()))
                return (p);
        }
        return (null);
    }

    public void cancelAbility() {
        this.abilityCancelled = true;
        invisiblePlayer.remove(this);

        if (scheduler != null) {
            scheduler.cancel();
        }

        final Player player = this.getPlayer();
        if (player == null) return;
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    public static void onJoin(Player player) {
        restoreInvisiblePlayer(player);
        hidePlayerArmor(player);
    }

    private static void restoreInvisiblePlayer(Player player) {
        for (InvisiblePlayer ip : InvisiblePlayer.invisiblePlayer) {
            if (player.getUniqueId().equals(ip.getPlayer().getUniqueId())) {
                ip.setPlayer(player);
            }
        }
    }

    public static void onDamage(Entity entity) {
        if (entity instanceof final Player player) {
           final InvisiblePlayer invisiblePlayer = InvisiblePlayer.getInvisiblePlayer(player);
           if (invisiblePlayer == null) return;

           invisiblePlayer.cancelAbility();
        }
    }

    public static void hidePlayerArmor(Player player) {
        for (InvisiblePlayer ip : InvisiblePlayer.invisiblePlayer) {
            if (ip.getAbilityLevel() > 1 && !ip.abilityCancelled &&
                !player.getUniqueId().equals(ip.getPlayer().getUniqueId())) {
                ArmorHider.hideArmor(player, ip.getPlayer());
            }
        }
    }
}
