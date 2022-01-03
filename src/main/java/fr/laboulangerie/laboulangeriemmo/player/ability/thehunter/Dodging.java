package fr.laboulangerie.laboulangeriemmo.player.ability.thehunter;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import fr.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.player.EntityHuman;

public class Dodging extends AbilityExecutor {

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.LEFT_CLICK_AIR;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        return true;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        PlayerInteractEvent event = (PlayerInteractEvent) baseEvent;
        Player player = event.getPlayer();
        EntityHuman human = (EntityHuman) ((CraftPlayer) event.getPlayer()).getHandle();
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
        float f2 = -MathHelper.sin(yRot * 0.017453292F) * MathHelper.cos(xRot * 0.017453292F);
        float f3 = -MathHelper.sin(xRot * 0.017453292F);
        float f4 = MathHelper.cos(yRot * 0.017453292F) * MathHelper.cos(xRot * 0.017453292F);
        float f5 = MathHelper.c(f2 * f2 + f3 * f3 + f4 * f4);
        float f6 = 3.0F * ((1.0F + power) / 4.0F);

        f2 *= f6 / f5;
        f3 *= f6 / f5;
        f4 *= f6 / f5;
        //human.i((double) f2, (double) f3, (double) f4); // = human.push doesn't work but next line replaced it maybe try again in 1.18
        player.setVelocity(new Vector((double) f2, (double) f3, (double) f4));
        human.s(20);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1, 1);
    }
}
