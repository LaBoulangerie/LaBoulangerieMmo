package net.laboulangerie.laboulangeriemmo.player.ability.thehunter;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.thehunter.firebow.FireArrow;
import net.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event;

import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.player.ability.AbilityTrigger;

public class FireBow extends AbilityExecutor {

    @Override
    public AbilityTrigger getAbilityTrigger() {
        return AbilityTrigger.COMBO;
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;

        boolean hasFlameBow = event.getPlayer().getInventory().contains(Material.BOW)
            && event.getPlayer().getInventory().all(Material.BOW).values().stream()
                .map(item -> item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.ARROW_FIRE))
                .reduce(false, (result, hasEnchant)-> (result == true) || hasEnchant);

        return new KeyStreak(ComboKey.RIGHT, ComboKey.LEFT, ComboKey.LEFT).match(event.getKeyStreak())
            && hasFlameBow;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        final ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        final Player player = event.getPlayer();

        int abilityLevel = 1;

        if (level >= 50)
            abilityLevel = 3;
        else if (level >= 30)
            abilityLevel = 2;

        FireArrow.fireArrow.add(new FireArrow(player, abilityLevel));
    }

    public static void onArrowHit(Player player, Arrow arrow, Block block, Entity entity) {
        FireArrow fireArrow = null;

        for (FireArrow fa : FireArrow.fireArrow) {
            if (fa.getShooter().getUniqueId().equals(player.getUniqueId()) && arrow == fa.getArrow()) {
                fireArrow = fa;
            }
        }

        if (fireArrow == null) return;

        if (entity != null)
            onEntityHit(fireArrow, entity);
        else if (block != null)
            onBlockHit(fireArrow, block);

        FireArrow.fireArrow.remove(fireArrow);
    }

    private static void onEntityHit(FireArrow fireArrow, Entity entity) {
        final int level = fireArrow.getAbilityLevel();

        if (level == 1) return;
        if (level == 2) explosion(fireArrow.getShooter(), entity.getLocation(), 2);
        if (level == 3) explosion(fireArrow.getShooter(), entity.getLocation(), 4);
    }

    private static void onBlockHit(FireArrow fireArrow, Block block) {
        final int level = fireArrow.getAbilityLevel();

        final World world = block.getWorld();

        if (level == 1) {
            for (int x = block.getX() - 1; x < block.getX() + 1; x++) {
                for (int y = block.getY() - 1; y < block.getY() + 1; y++) {
                    for (int z = block.getZ() - 1; z < block.getZ() + 1; z++) {
                        if (world.getBlockAt(x, y, z).getType().equals(Material.AIR)) {
                            world.getBlockAt(x, y, z).setType(Material.FIRE);
                        }
                    }
                }
            }
        }
        if (level == 2) explosion(fireArrow.getShooter(), block.getLocation(), 2);
        if (level == 3) explosion(fireArrow.getShooter(), block.getLocation(), 4);
    }

    private static void explosion(Player shooter, Location location, int power) {
        final World world = location.getWorld();
        TNTPrimed tnt = (TNTPrimed) world.spawnEntity(location, EntityType.PRIMED_TNT);
        tnt.setIsIncendiary(true);
        tnt.setSource(shooter);
        tnt.setFuseTicks(0);
        tnt.setYield(power);
    }
}
