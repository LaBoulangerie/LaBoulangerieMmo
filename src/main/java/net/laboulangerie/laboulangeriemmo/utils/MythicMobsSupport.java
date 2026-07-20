package net.laboulangerie.laboulangeriemmo.utils;

import java.util.Optional;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.MobHeadsRegistry;

public class MythicMobsSupport {
    public static Optional<MythicMobContext> inspect(Entity entity) {
        Optional<ActiveMob> mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        if (mythicMob.isEmpty() || !(entity instanceof LivingEntity livingEntity)) return Optional.empty();

        Optional<net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.MobVisualProfile> disguise =
                Optional.empty();
        if (LaBoulangerieMmo.LIBSDISGUISES_SUPPORT) {
            disguise = LibsDisguisesSupport.getVisualProfile(entity);
        }
        return Optional.of(new MythicMobContext(mythicMob.get().getType().getInternalName(),
                MobHeadsRegistry.getVisualProfile(livingEntity), disguise));
    }
}
