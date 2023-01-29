package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.firebow;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;

public class FireArrow {

    public static final Set<FireArrow> fireArrow = new HashSet<>();

    private final Player shooter;

    private final int abilityLevel;

    private AbstractArrow arrow;

    public static void shoot(Player player, AbstractArrow arrow) {
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

    public AbstractArrow getArrow() {
        return arrow;
    }
}
