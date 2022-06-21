package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.firebow.FireArrow;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;

public class FireBow extends AbilityExecutor {

    public FireBow(AbilityArchetype archetype) {
        super(archetype);
    }

    private static final int SMALL_EXPLOSION_POWER = 2;
    private static final int LARGE_EXPLOSION_POWER = 3;

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

        if (level >= getTier(2))
            abilityLevel = 3;
        else if (level >= getTier(1))
            abilityLevel = 2;

        for (FireArrow fa : FireArrow.fireArrow) {
            if (fa.getShooter().getUniqueId().equals(player.getUniqueId())) {
                return;
            }
        }
        FireArrow.fireArrow.add(new FireArrow(player, abilityLevel));
    }

    public static void onPlayerShoot(Player player, Arrow arrow) {
    	FireArrow fireArrow = null;
        for (FireArrow fa : FireArrow.fireArrow) {
            if (fa.getShooter().getUniqueId().equals(player.getUniqueId()) && arrow == fa.getArrow()) {
            	fireArrow = fa;
                if (!(fireArrow.getShooter().getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.ARROW_FIRE))) {
                	FireArrow.fireArrow.remove(fireArrow);
                }
            }
        }
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

    private static void onEntityHit(FireArrow fireArrow, Entity entity)  {
    	final int level = fireArrow.getAbilityLevel();

        if (level == 1) putFire(entity.getLocation());
        if (level == 2) explosion(fireArrow.getShooter(), entity.getLocation(), SMALL_EXPLOSION_POWER);
        if (level == 3) explosion(fireArrow.getShooter(), entity.getLocation(), LARGE_EXPLOSION_POWER);
    }

    private static void onBlockHit(FireArrow fireArrow, Block block) {
        final int level = fireArrow.getAbilityLevel();

        if (level == 1) putFire(block.getLocation());
        if (level == 2) explosion(fireArrow.getShooter(), block.getLocation(), SMALL_EXPLOSION_POWER);
        if (level == 3) explosion(fireArrow.getShooter(), block.getLocation(), LARGE_EXPLOSION_POWER);
    }

    private static void putFire(Location location) {
        final World world = location.getWorld();
        final Random RNG = new Random();

        for (int x = location.getBlockX() - 1; x <= location.getBlockX() + 1; x++) {
            for (int y = location.getBlockY() - 1; y <= location.getBlockY() + 1; y++) {
                for (int z = location.getBlockZ() - 1; z <= location.getBlockZ() + 1; z++) {
                    if (world.getBlockAt(x, y, z).getType().equals(Material.AIR) && RNG.nextInt(4) < 3) {
                        world.getBlockAt(x, y, z).setType(Material.FIRE);
                    }
                }
            }
        }
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
