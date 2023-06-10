package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.destroystokyo.paper.MaterialSetTag;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;

public class Stun extends AbilityExecutor {

    private final static float TIER_3_CHANCE = 0.2f;
    private final static float TIER_2_CHANCE = 0.1f;
    private final static float TIER_1_CHANCE = 0.05f;

    private final static PotionEffect reducedJumpEffect =
            new PotionEffect(PotionEffectType.JUMP, PotionEffect.INFINITE_DURATION, 253, true, false);

    private final static PotionEffect slownessEffect =
            new PotionEffect(PotionEffectType.SLOW, PotionEffect.INFINITE_DURATION, 3, true, false);

    private final static PotionEffect blindnessEffect =
            new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0, true, false);

    public Stun(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) baseEvent;
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity)) return false;

        Player player = (Player) event.getDamager();
        Material heldItemMat = player.getInventory().getItem(player.getInventory().getHeldItemSlot()).getType();

        return event.isCritical() && MaterialSetTag.ITEMS_AXES.isTagged(heldItemMat);
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) baseEvent;
        LivingEntity entity = (LivingEntity) event.getEntity();

        float random = (float) Math.random();
        boolean shouldStun = false;
        int durationSeconds = 2;

        if (level >= getTier(2) && random <= TIER_3_CHANCE) {
            shouldStun = true;
            durationSeconds = 10;
        } else if (level >= getTier(1) && random <= TIER_2_CHANCE) {
            shouldStun = true;
            durationSeconds = 5;
        } else if (random <= TIER_1_CHANCE) shouldStun = true;

        if (shouldStun) {
            entity.addPotionEffect(reducedJumpEffect);
            entity.addPotionEffect(slownessEffect);
            entity.addPotionEffect(blindnessEffect);

            Bukkit.getScheduler().runTaskLater(LaBoulangerieMmo.PLUGIN, new Runnable() {
                @Override
                public void run() {
                    entity.removePotionEffect(PotionEffectType.JUMP);
                    entity.removePotionEffect(PotionEffectType.SLOW);
                    entity.removePotionEffect(PotionEffectType.BLINDNESS);
                }
            }, durationSeconds * 20);
        }
    }

}
