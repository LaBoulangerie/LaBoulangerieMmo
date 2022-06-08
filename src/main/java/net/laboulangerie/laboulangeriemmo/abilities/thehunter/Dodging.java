package net.laboulangerie.laboulangeriemmo.abilities.thehunter;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import net.minecraft.world.entity.player.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import net.laboulangerie.laboulangeriemmo.abilities.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.abilities.AbilityTrigger;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;
import net.minecraft.util.Mth;

public class Dodging extends AbilityExecutor {

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.COMBO;
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
         * f -> yRot
         * f1 -> xRot
         */
        float power = 1;
        float yRot = human.getYRot();
        float xRot = human.getXRot();
        float f2 = -Mth.sin(yRot * 0.017453292F) * Mth.cos(xRot * 0.017453292F);
        float f3 = -Mth.sin(xRot * 0.017453292F);
        float f4 = Mth.cos(yRot * 0.017453292F) * Mth.cos(xRot * 0.017453292F);
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
