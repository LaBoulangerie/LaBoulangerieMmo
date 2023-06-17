package net.laboulangerie.laboulangeriemmo.utils;

import java.util.Optional;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.laboulangerie.laboulangeriemmo.api.player.GrindingCategory;
import net.laboulangerie.laboulangeriemmo.listener.GrindingListener;

public class MythicMobsSupport {
    public static boolean tryToGiveMythicReward(Entity entity, Player killer) {
        Optional<ActiveMob> mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());

        if (mythicMob.isPresent()) {
            GrindingListener.giveReward(killer, GrindingCategory.KILL,
                    "MYTHICMOBS_" + mythicMob.get().getType().getInternalName().toUpperCase(), false);
        }
        return mythicMob.isPresent();
    }
}
