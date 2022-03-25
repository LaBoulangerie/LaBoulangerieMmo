package net.laboulangerie.laboulangeriemmo.core.thehunter.firebow;

import net.laboulangerie.laboulangeriemmo.core.thehunter.hiding.InvisiblePlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class FireArrow {

    public static final Set<FireArrow> fireArrow = new HashSet<>();

    private final Player shooter;

    private final int abilityLevel;

    private Arrow arrow;

    public static void shoot(Player player, Arrow arrow) {
        for (FireArrow fa : fireArrow) {
            if (fa.shooter.getUniqueId().equals(player.getUniqueId())) {
                fa.arrow = arrow;
            }
        }
    }

    public FireArrow (Player shooter, int abilityLevel) {
        this.shooter = shooter;
        this.abilityLevel = abilityLevel;
    }

    public Player getShooter() {
        return shooter;
    }

    public int getAbilityLevel() {
        return abilityLevel;
    }

    public Arrow getArrow() {
        return arrow;
    }

}
