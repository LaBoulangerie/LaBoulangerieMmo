package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.destroystokyo.paper.MaterialSetTag;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;

public class Stun extends AbilityExecutor {

    private final static float TIER_3_CHANCE = 0.2f;
    private final static float TIER_2_CHANCE = 0.1f;
    private final static float TIER_1_CHANCE = 0.05f;

    private final static PotionEffect blindnessEffect = new PotionEffect(PotionEffectType.BLINDNESS,
            PotionEffect.INFINITE_DURATION, 0, true, false);

    public Stun(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) baseEvent;
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity))
            return false;

        Player player = (Player) event.getDamager();
        ItemStack heldItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
        if (heldItem == null)
            return false;

        return event.isCritical() && MaterialSetTag.ITEMS_AXES.isTagged(heldItem.getType());
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) baseEvent;
        LivingEntity entity = (LivingEntity) event.getEntity();

        float random = (float) Math.random();
        boolean shouldStun = false;
        boolean shouldBlind = false;
        int durationSeconds = 2;
        PotionEffect slownessEffect = new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 1, true,
                false);

        if (level >= getTier(2) && random <= TIER_3_CHANCE) {
            shouldStun = true;
            shouldBlind = true;
            durationSeconds = 3;
            slownessEffect = new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 3, true, false);
        } else if (level >= getTier(1) && random <= TIER_2_CHANCE) {
            shouldStun = true;
            shouldBlind = true;
            durationSeconds = 5;
            slownessEffect = new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 2, true, false);

        } else if (random <= TIER_1_CHANCE)
            shouldStun = true;

        if (shouldStun) {
            EffectRegistry.playEffect("stun", entity);
            entity.addPotionEffect(slownessEffect);
            if (shouldBlind)
                entity.addPotionEffect(blindnessEffect);

            Bukkit.getScheduler().runTaskLater(LaBoulangerieMmo.PLUGIN, new Runnable() {
                @Override
                public void run() {
                    entity.removePotionEffect(PotionEffectType.JUMP_BOOST);
                    entity.removePotionEffect(PotionEffectType.SLOWNESS);
                    entity.removePotionEffect(PotionEffectType.BLINDNESS);
                }
            }, durationSeconds * 20);
        }
    }

}
