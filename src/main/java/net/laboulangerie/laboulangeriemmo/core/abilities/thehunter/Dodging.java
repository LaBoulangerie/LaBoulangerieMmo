package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class Dodging extends AbilityExecutor {

    public Dodging(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        return event.getKeyStreak().match(new KeyStreak(ComboKey.LEFT, ComboKey.LEFT, ComboKey.RIGHT));
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        org.bukkit.entity.Player player = event.getPlayer();
        Player human = (Player) ((CraftPlayer) event.getPlayer()).getHandle();
        /**
         * See ItemTrident.java in craftbukkit or TridentItem.java in paper
         * Mapping:
         * k -> power (originally the riptide level)
         * f -> xRot
         * f1 -> yRot
         */
        float power = 1;
        float xRot = human.getYRot();
        float yRot = Math.max(human.getXRot()-15, -90);
        float f2 = -Mth.sin(xRot * 0.017453292F) * Mth.cos(yRot * 0.017453292F);
        float f3 = -Mth.sin(yRot * 0.017453292F);
        float f4 = Mth.cos(xRot * 0.017453292F) * Mth.cos(yRot * 0.017453292F);
        float f5 = Mth.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
        float f6 = 3.0F * ((1.0F + power) / 4.0F);

        f2 *= f6 / f5;
        f3 *= f6 / f5;
        f4 *= f6 / f5;

        player.setVelocity(new Vector((double) f2, (double) f3, (double) f4));
        human.startAutoSpinAttack(20);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1, 1);
    }
}
