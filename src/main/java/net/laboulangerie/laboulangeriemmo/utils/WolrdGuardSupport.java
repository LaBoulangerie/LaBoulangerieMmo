
package net.laboulangerie.laboulangeriemmo.utils;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

/**
 * WolrdGuardSupport
 */
public class WolrdGuardSupport {
    public static StateFlag USE_ABILITY_FLAG = new StateFlag("ability-use", false);

    public static void enableSupport() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            registry.register(USE_ABILITY_FLAG);
        } catch (IllegalStateException e) {
            LaBoulangerieMmo.PLUGIN.getLogger()
                    .severe("Unable to register our custom flag, caused by incorrect load order:");
            LaBoulangerieMmo.PLUGIN.getLogger().severe(e.getMessage());
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("ability-use");

            if (existing instanceof StateFlag) USE_ABILITY_FLAG = (StateFlag) existing
            else {
                LaBoulangerieMmo.PLUGIN.getLogger().severe(
                        "Unable to register our custom flag due to a conflict with another plugin!");
            }
        }
    }

    public static boolean isOperationPermitted(Player player) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set =
                query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        return set.testState(WorldGuardPlugin.inst().wrapPlayer(player), USE_ABILITY_FLAG);
    }
}
